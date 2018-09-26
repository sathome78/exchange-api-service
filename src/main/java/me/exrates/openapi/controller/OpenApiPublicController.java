package me.exrates.openapi.controller;

import me.exrates.openapi.controller.advice.OpenApiError;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.dto.CandleChartItemReducedDto;
import me.exrates.openapi.model.dto.CoinmarketApiDto;
import me.exrates.openapi.model.dto.TradeHistoryDto;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.model.dto.openAPI.OrderBookItem;
import me.exrates.openapi.model.dto.openAPI.TickerJsonDto;
import me.exrates.openapi.model.enums.ErrorCode;
import me.exrates.openapi.model.enums.IntervalType;
import me.exrates.openapi.model.enums.OrderType;
import me.exrates.openapi.model.vo.BackDealInterval;
import me.exrates.openapi.model.web.BaseResponse;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.openapi.utils.OpenApiUtils.convertCurrencyPairName;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/public")
public class OpenApiPublicController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CurrencyService currencyService;

    /**
     * @api {get} /openapi/v1/public/ticker?currency_pair Ticker Info
     * @apiName Ticker
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Returns array of ticker info objects
     * @apiParam {String} currency_pair Currency pair name (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/public/ticker?currency_pair=btc_usd
     * @apiSuccess {Array} Ticker Infos result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Currency pair id
     * @apiSuccess {String} data.name Currency pair name
     * @apiSuccess {Number} data.last Price of last accepted order
     * @apiSuccess {Number} data.lowestAsk 	Lowest price of opened sell order
     * @apiSuccess {Number} data.highestBid Highest price of opened buy order
     * @apiSuccess {Number} data.percentChange Change for period, %
     * @apiSuccess {Number} data.baseVolume Volume of trade in base currency
     * @apiSuccess {Number} data.quoteVolume Volume of trade in quote currency
     * @apiSuccess {Number} data.high Highest price of accepted orders
     * @apiSuccess {Number} data.low Lowest price of accepted orders
     * * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 123,
     * "name": "currencyPairName",
     * "last": 12341,
     * "lowestAsk": 12342,
     * "highestBid":  12343
     * "percentChange":  1
     * "baseVolume": 10
     * "quoteVolume": 11
     * "high": 10
     * "low": 1
     * }
     * ]
     */
    @RequestMapping("/ticker")
    public List<TickerJsonDto> getDailyTicker(@RequestParam(value = "currency_pair", required = false) String currencyPair) {
        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = convertCurrencyPairName(currencyPair);
            validateCurrencyPair(currencyPairName);
        }
        return formatCoinmarketData(orderService.getDailyCoinmarketData(currencyPairName));
    }

    private void validateCurrencyPair(String currencyPairName) {
        currencyService.findCurrencyPairIdByName(currencyPairName);
    }


    private List<TickerJsonDto> formatCoinmarketData(List<CoinmarketApiDto> data) {
        return data.stream().map(TickerJsonDto::new).collect(toList());
    }

    /**
     * @api {get} /openapi/v1/public/orderbook/:currency_pair?order_type Order Book
     * @apiName Order Book
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Books Order
     * @apiParam {String} order_type Order type (BUY or SELL) (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/public/orderbook/btc_usd/?order_type=SELL
     * @apiSuccess {Map} Object with SELL and BUY fields, each containing array of open orders info objects
     * (sorted by price - SELL ascending, BUY descending).
     * amount -	order amount in base currency
     * rate	- exchange rate
     */
    @RequestMapping("/orderbook/{currency_pair}")
    public Map<OrderType, List<OrderBookItem>> getOrderBook(@PathVariable(value = "currency_pair") String currencyPair,
                                                            @RequestParam(value = "order_type", required = false) OrderType orderType) {
        String currencyPairName = convertCurrencyPairName(currencyPair);
        return orderService.getOrderBook(currencyPairName, orderType);
    }

    /**
     * @api {get} /openapi/v1/public/history/{currency_pair}?from_date&to_date&limit Trade History
     * @apiName Trade History
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Provides collection of trade info objects
     * @apiParam {LocalDate} from_date start date of search (date format: yyyy-MM-dd)
     * @apiParam {LocalDate} to_date end date of search (date format: yyyy-MM-dd)
     * @apiParam {Integer} limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
     * @apiParamExample Request Example:
     * openapi/v1/public/history/btc_usd?from_date=2018-09-01&to_date=2018-09-05&limit=20
     * @apiSuccess {Array} Array of trade info objects
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.order_id Order id
     * @apiSuccess {String} data.date_acceptance Order acceptance date
     * @apiSuccess {String} data.date_creation Order creation date
     * @apiSuccess {Number} data.amount Order amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.total Total sum
     * @apiSuccess {Number} data.commission commission
     * @apiSuccess {String} data.order_type Order type (BUY or SELL)
     */
    @GetMapping(value = "/history/{currency_pair}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TradeHistoryDto>>> getTradeHistory(@PathVariable(value = "currency_pair") String currencyPair,
                                                                               @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                               @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                               @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest().body(BaseResponse.error("From date is after to date"));
        }
        if (nonNull(limit) && limit <= 0) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Limit value equals or less than zero"));
        }

        final String transformedCurrencyPair = convertCurrencyPairName(currencyPair);

        return ResponseEntity.ok(BaseResponse.success(orderService.getTradeHistory(transformedCurrencyPair, fromDate, toDate, limit)));
    }

    /**
     * @api {get} /openapi/v1/public/currency_pairs Currency Pairs
     * @apiName Currency Pairs
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Provides collection of currency pairs
     * @apiParamExample Request Example:
     * openapi/v1/public/currency_pairs
     * @apiSuccess {Array} Array of currency pairs
     * @apiSuccess {Object} data Container object
     * @apiSuccess {String} data.name Currency pair name
     * @apiSuccess {String} data.url_symbol URL symbol (name to be passed as URL parameter or path variable)
     */
    @RequestMapping("/currency_pairs")
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return currencyService.findActiveCurrencyPairs();
    }

    /**
     * @api {get} /openapi/v1/public/{currency_pair}/candle_chart?interval_type&interval_value Data for candle chart
     * @apiName Data for candle chart
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Data for candle chart
     * @apiParam {String} interval_type type of interval (valid values: "HOUR", "DAY", "MONTH", "YEAR")
     * @apiParam {Integer} interval_value value of interval
     * @apiParamExample Request Example:
     * /openapi/v1/public/btc_usd/candle_chart?interval_type=DAY&interval_value=7
     * @apiSuccess {Array} chartData Request result
     * @apiSuccess {Object} data Candle chart data item
     * @apiSuccess {Object} data.beginPeriod beginning of period as Java8 LocalDateTime
     * @apiSuccess {Object} data.endPeriod end of period as Java8 LocalDateTime
     * @apiSuccess {Number} data.openRate open rate
     * @apiSuccess {Number} data.closeRate close rate
     * @apiSuccess {Number} data.lowRate low rate
     * @apiSuccess {Number} data.highRate high rate
     * @apiSuccess {Number} data.baseVolume base amount of order
     * @apiSuccess {Number} data.beginDate same as beginPeriod, different format
     * @apiSuccess {Number} data.endDate same as endPeriod, different format
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":1472132318000,
     * "endDate":1472132378000
     * },
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":1472132378000,
     * "endDate":1472132438000
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
    @GetMapping(value = "/{currency_pair}/candle_chart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CandleChartItemReducedDto>>> getCandleChartData(@PathVariable(value = "currency_pair") String currencyPair,
                                                                                            @RequestParam(value = "interval_type") IntervalType intervalType,
                                                                                            @RequestParam(value = "interval_value") Integer intervalValue) {
        final CurrencyPair currencyPairByName = currencyService.getCurrencyPairByName(convertCurrencyPairName(currencyPair));
        final BackDealInterval interval = new BackDealInterval(intervalValue, intervalType);

        List<CandleChartItemReducedDto> resultList = orderService.getDataForCandleChart(currencyPairByName, interval).stream()
                .map(CandleChartItemReducedDto::new)
                .collect(toList());
        return ResponseEntity.ok(BaseResponse.success(resultList));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, MethodArgumentTypeMismatchException exception) {
        String detail = "Invalid param value : " + exception.getParameter().getParameterName();
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), detail);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception);
    }


    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }
}
