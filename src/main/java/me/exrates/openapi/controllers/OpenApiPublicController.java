package me.exrates.openapi.controllers;

import me.exrates.openapi.controllers.advice.OpenApiError;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;
import me.exrates.openapi.models.dto.CandleChartItemReducedDto;
import me.exrates.openapi.models.dto.TradeHistoryDto;
import me.exrates.openapi.models.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.models.dto.openAPI.OrderBookItem;
import me.exrates.openapi.models.dto.openAPI.TickerDto;
import me.exrates.openapi.models.enums.ErrorCode;
import me.exrates.openapi.models.enums.IntervalType;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.vo.BackDealInterval;
import me.exrates.openapi.models.web.BaseResponse;
import me.exrates.openapi.services.CurrencyService;
import me.exrates.openapi.services.OrderService;
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
import static me.exrates.openapi.converters.CurrencyPairConverter.convert;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/public")
public class OpenApiPublicController {

    private final OrderService orderService;
    private final CurrencyService currencyService;

    @Autowired
    public OpenApiPublicController(OrderService orderService,
                                   CurrencyService currencyService) {
        this.orderService = orderService;
        this.currencyService = currencyService;
    }

    /**
     * @api {get} /openapi/v1/public/ticker/{currency_1}/{currency_2} Ticker Info
     * @apiName Ticker info
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Returns array with one ticker object element
     * @apiParamExample Request Example:
     * /openapi/v1/public/ticker/btc/usd
     * @apiSuccess {Array} Ticker Info result
     * @apiSuccess {Object}     data                    Container object
     * @apiSuccess {Integer}    data.id                 Currency pair id
     * @apiSuccess {String}     data.name               Currency pair name
     * @apiSuccess {Number}     data.last               Price of last accepted order
     * @apiSuccess {Number}     data.lowest_ask 	    Lowest price of opened sell order
     * @apiSuccess {Number}     data.highest_bid        Highest price of opened buy order
     * @apiSuccess {Number}     data.percent_change     Change for period, %
     * @apiSuccess {Number}     data.base_volume        Volume of trade in base currency
     * @apiSuccess {Number}     data.quote_volume       Volume of trade in quote currency
     * @apiSuccess {Number}     data.high               Highest price of accepted orders
     * @apiSuccess {Number}     data.low                Lowest price of accepted orders
     * * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 1,
     * "name": "btc/usd",
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
    @GetMapping(value = "/ticker/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TickerDto>>> getTicker(@PathVariable("currency_1") String currency1,
                                                                   @PathVariable("currency_2") String currency2) {
        String pairName = convert(currency1, currency2);

        List<TickerDto> result = orderService.getDailyCoinmarketData(pairName).stream()
                .map(TickerDto::new)
                .collect(toList());

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * @api {get} /openapi/v1/public/tickers Tickers Info
     * @apiName Tickers info
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Returns array with tickers object elements
     * @apiParamExample Request Example:
     * /openapi/v1/public/tickers
     * @apiSuccess {Array} Ticker Info result
     * @apiSuccess {Object}     data                    Container object
     * @apiSuccess {Integer}    data.id                 Currency pair id
     * @apiSuccess {String}     data.name               Currency pair name
     * @apiSuccess {Number}     data.last               Price of last accepted order
     * @apiSuccess {Number}     data.lowest_ask 	    Lowest price of opened sell order
     * @apiSuccess {Number}     data.highest_bid        Highest price of opened buy order
     * @apiSuccess {Number}     data.percent_change     Change for period, %
     * @apiSuccess {Number}     data.base_volume        Volume of trade in base currency
     * @apiSuccess {Number}     data.quote_volume       Volume of trade in quote currency
     * @apiSuccess {Number}     data.high               Highest price of accepted orders
     * @apiSuccess {Number}     data.low                Lowest price of accepted orders
     * * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 1,
     * "name": "btc/usd",
     * "last": 12341,
     * "lowestAsk": 12342,
     * "highestBid":  12343
     * "percentChange":  1
     * "baseVolume": 10
     * "quoteVolume": 11
     * "high": 10
     * "low": 1
     * },
     * {
     * "id": 2,
     * "name": "eth/usd",
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
    @GetMapping(value = "/tickers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TickerDto>>> getTickers() {
        List<TickerDto> result = orderService.getDailyCoinmarketData(null).stream()
                .map(TickerDto::new)
                .collect(toList());

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * @api {get} /openapi/v1/public/order_book/{currency_1}/{currency_2}?order_type&limit Order Book Info
     * @apiName Order Book Info
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Books Order
     * @apiParam {String} order_type Order type (BUY or SELL) (optional)
     * @apiParam {Integer} limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/public/order_book/btc/usd?order_type=SELL&limit=20
     * @apiSuccess {Map} Object with SELL and BUY fields, each containing array of open orders info objects (sorted by price - SELL ascending, BUY descending).
     * @apiSuccess {Object}     data                    Container object
     * @apiSuccess {Number}     data.amount             Order amount in base currency
     * @apiSuccess {Number}     data.rate               Exchange rate
     */
    @GetMapping(value = "/order_book/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Map<OrderType, List<OrderBookItem>>>> getOrderBook(@PathVariable("currency_1") String currency1,
                                                                                          @PathVariable("currency_2") String currency2,
                                                                                          @RequestParam(value = "order_type", required = false) OrderType orderType,
                                                                                          @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (nonNull(limit) && limit <= 0) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Limit value equals or less than zero"));
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderBook(pairName, orderType, limit)));
    }

    /**
     * @api {get} /openapi/v1/public/history/{currency_1}/{currency_2}?from_date&to_date&limit Trade History Info
     * @apiName Trade History Info
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Returns array of trade info objects
     * @apiParam {LocalDate}    from_date   start date of search (date format: yyyy-MM-dd)
     * @apiParam {LocalDate}    to_date     end date of search (date format: yyyy-MM-dd)
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
     * @apiParamExample Request Example:
     * openapi/v1/public/history/btc/usd?from_date=2018-09-01&to_date=2018-09-05&limit=20
     * @apiSuccess {Array} Array of trade info objects
     * @apiSuccess {Object}     data                    Container object
     * @apiSuccess {Integer}    data.order_id           Order id
     * @apiSuccess {String}     data.date_acceptance    Order acceptance date
     * @apiSuccess {String}     data.date_creation      Order creation date
     * @apiSuccess {Number}     data.amount             Order amount in base currency
     * @apiSuccess {Number}     data.price              Exchange rate
     * @apiSuccess {Number}     data.total              Total sum
     * @apiSuccess {Number}     data.commission         Commission
     * @apiSuccess {String}     data.order_type         Order type (BUY or SELL)
     */
    @GetMapping(value = "/history/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TradeHistoryDto>>> getTradeHistory(@PathVariable("currency_1") String currency1,
                                                                               @PathVariable("currency_2") String currency2,
                                                                               @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                               @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                               @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest().body(BaseResponse.error("From date is after to date"));
        }
        if (nonNull(limit) && limit <= 0) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Limit value equals or less than zero"));
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(BaseResponse.success(orderService.getTradeHistory(pairName, fromDate, toDate, limit)));
    }

    /**
     * @api {get} /openapi/v1/public/{currency_pairs} Currency Pairs Info
     * @apiName Currency Pairs Info
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Provides array of currency pairs
     * @apiParamExample Request Example:
     * openapi/v1/public/currency_pairs
     * @apiSuccess {Array}  Array of currency pairs
     * @apiSuccess {Object}  data             Container object
     * @apiSuccess {String}  data.name        Currency pair name
     * @apiSuccess {String}  data.url_symbol  URL symbol (name to be passed as URL parameter or path variable)
     */
    @GetMapping(value = "/currency_pairs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CurrencyPairInfoItem>>> findActiveCurrencyPairs() {
        return ResponseEntity.ok(BaseResponse.success(currencyService.findActiveCurrencyPairs()));
    }

    /**
     * @api {get} /openapi/v1/public/candle_chart/{currency_1}/{currency_2}?interval_type&interval_value Data for candle chart
     * @apiName Data for candle chart
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Data for candle chart
     * @apiParam {String} interval_type type of interval (valid values: "HOUR", "DAY", "MONTH", "YEAR")
     * @apiParam {Integer} interval_value value of interval
     * @apiParamExample Request Example:
     * /openapi/v1/public/candle_chart/btc/usd?interval_type=DAY&interval_value=7
     * @apiSuccess {Array} chartData Request result
     * @apiSuccess {Object} data Candle chart data item
     * @apiSuccess {Number}     data.openRate       Open rate
     * @apiSuccess {Number}     data.closeRate      Close rate
     * @apiSuccess {Number}     data.lowRate        Low rate
     * @apiSuccess {Number}     data.highRate       High rate
     * @apiSuccess {Number}     data.baseVolume     Base amount of order
     * @apiSuccess {String}     data.beginDate      Begin date
     * @apiSuccess {String}     data.endDate        End date
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":2018-09-01,
     * "endDate":2018-09-05
     * },
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":2018-09-01,
     * "endDate":2018-09-05
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
    @GetMapping(value = "/candle_chart/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CandleChartItemReducedDto>>> getCandleChartData(@PathVariable("currency_1") String currency1,
                                                                                            @PathVariable("currency_2") String currency2,
                                                                                            @RequestParam(value = "interval_type") IntervalType intervalType,
                                                                                            @RequestParam(value = "interval_value") Integer intervalValue) {
        String pairName = convert(currency1, currency2);
        BackDealInterval interval = new BackDealInterval(intervalValue, intervalType);

        List<CandleChartItemReducedDto> resultList = orderService.getDataForCandleChart(pairName, interval).stream()
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
