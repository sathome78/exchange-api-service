package me.exrates.openapi.controller;

import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.model.enums.ErrorCode;
import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;
import me.exrates.openapi.controller.advice.OpenApiError;
import me.exrates.openapi.model.dto.CoinmarketApiDto;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.model.dto.openAPI.OrderBookItem;
import me.exrates.openapi.model.dto.openAPI.OrderHistoryItem;
import me.exrates.openapi.model.dto.openAPI.TickerJsonDto;
import me.exrates.openapi.model.enums.OrderType;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.openapi.utils.OpenApiUtils.formatCurrencyPairNameParam;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/openapi/v1/public")
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
            currencyPairName = formatCurrencyPairNameParam(currencyPair);
            validateCurrencyPair(currencyPairName);
        }
        return formatCoinmarketData(orderService.getDailyCoinmarketData(currencyPairName));
    }

    //+
    private void validateCurrencyPair(String currencyPairName) {
        currencyService.findCurrencyPairIdByName(currencyPairName);
    }


    //+
    private List<TickerJsonDto> formatCoinmarketData(List<CoinmarketApiDto> data) {
        return data.stream().map(TickerJsonDto::new).collect(Collectors.toList());
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
        String currencyPairName = formatCurrencyPairNameParam(currencyPair);
        return orderService.getOrderBook(currencyPairName, orderType);
    }

    /**
     * @api {get} /openapi/v1/public/history/{currency_pair}?hour Deal history
     * @apiName Deal History
     * @apiGroup Public API
     * @apiPermission user
     * @apiDescription Provides collection of recent deal info objects
     * @apiParam {String} period period (available values: minute, hour, day, default: hour) (optional)
     * @apiParamExample Request Example:
     * openapi/v1/public/history/btc_usd?hour=1
     * @apiSuccess {Array} Array of recent deals info objects
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.order_id Order id
     * @apiSuccess {Number} data.date_acceptance Order acceptance date (as UNIX timestamp)
     * @apiSuccess {Number} data.amount Order amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {String} data.order_type Order type (BUY or SELL)
     */
    @RequestMapping("/history/{currency_pair}")
    public List<OrderHistoryItem> getRecentHistory(@PathVariable(value = "currency_pair") String currencyPair,
                                                   @RequestParam(required = false, defaultValue = "hour") String period) {

        String currencyPairName = formatCurrencyPairNameParam(currencyPair);
        return orderService.getRecentOrderHistory(currencyPairName, period);
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
