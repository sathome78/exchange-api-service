package me.exrates.openapi.controller;

import me.exrates.openapi.controller.advice.OpenApiError;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;
import me.exrates.openapi.model.dto.TransactionDto;
import me.exrates.openapi.model.dto.UserTradeHistoryDto;
import me.exrates.openapi.model.dto.openAPI.OpenApiCommissionDto;
import me.exrates.openapi.model.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.model.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.model.enums.ErrorCode;
import me.exrates.openapi.model.web.BaseResponse;
import me.exrates.openapi.service.OrderService;
import me.exrates.openapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

import static java.util.Objects.nonNull;
import static me.exrates.openapi.converters.CurrencyPairConverter.convert;
import static me.exrates.openapi.utils.ValidationUtil.validateNaturalInt;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/user")
public class OpenApiUserInfoController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

    /**
     * @apiDefine NonPublicAuth
     *  See Authentication API doc section
     */

    /**
     * @api {get} /openapi/v1/user/balances User Balances
     * @apiName User Balances
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of wallet objects
     * @apiParamExample Request Example:
     * /openapi/v1/user/balances
     * @apiSuccess {Array} Wallet objects result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {String} data.currencyName Name of currency
     * @apiSuccess {Number} data.activeBalance Balance that is available for spending
     * @apiSuccess {Number} data.reservedBalance Balance reserved for orders or withdraw
     */
    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    /**
     * @api {get} /openapi/v1/user/orders/open?currency_pair User's open orders
     * @apiName Open orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user open orders
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/open?currency_pair=btc_usd
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/open", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userOpenOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair) {

        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = convert(currencyPair);
        }
        return orderService.getUserOpenOrders(currencyPairName);
    }

    /**
     * @api {get} /openapi/v1/user/orders/closed?currency_pair&limit&offset User's closed orders
     * @apiName Closed orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user closed orders sorted by creation time
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
     * @apiParam {Integer} offset Number of orders skipped (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/closed?currency_pair=btc_usd&limit=100&offset=10
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userClosedOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                @RequestParam(required = false) Integer limit,
                                                @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair) ? convert(currencyPair) : null;

        validateNaturalInt(limit);
        validateNaturalInt(offset);

        return orderService.getUserClosedOrders(currencyPairName, limit, offset);
    }

    /**
     * @api {get} /openapi/v1/user/orders/canceled?currency_pair&limit&offset User's canceled orders
     * @apiName Canceled orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user canceled orders sorted by creation time
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
     * @apiParam {Integer} offset Number of orders skipped (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/canceled?currency_pair=btc_usd&limit=100&offset=10
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/canceled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<UserOrdersDto>>> userCanceledOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                                                @RequestParam(required = false) Integer limit,
                                                                                @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair) ? convert(currencyPair) : null;

        validateNaturalInt(limit);
        validateNaturalInt(offset);

        return ResponseEntity.ok(BaseResponse.success(orderService.getUserCanceledOrders(currencyPairName, limit, offset)));
    }

    /**
     * @api {get} /openapi/v1/user/commissions User’s commission rates
     * @apiName Commissions
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns info on user’s commission rates
     * (as per cent - for example, 0.5 rate means 0.5% of amount) by operation type.
     * Commissions for orders (sell and buy) are calculated and withdrawn from amount in quote currency.
     * @apiParamExample Request Example:
     * /openapi/v1/user/commissions
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.input Commission for input operations
     * @apiSuccess {Number} data.output Commission for output operations
     * @apiSuccess {Number} data.sell Commission for sell operations
     * @apiSuccess {Number} data.buy Commission for buy operations
     * @apiSuccess {Number} data.transfer Commission for transfer operations
     */
    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OpenApiCommissionDto getCommissions() {
        return new OpenApiCommissionDto(orderService.getAllCommissions());
    }

    /**
     * @api {get} /openapi/v1/user/history/{currency_pair}/trades?from_date&to_date&limit User trade history
     * @apiName User Trade History
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Provides collection of user trade info objects
     * @apiParam {LocalDate} from_date start date of search (date format: yyyy-MM-dd)
     * @apiParam {LocalDate} to_date end date of search (date format: yyyy-MM-dd)
     * @apiParam {Integer} limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
     * @apiParamExample Request Example:
     * openapi/v1/user/history/btc_usd/trades?from_date=2018-09-01&to_date=2018-09-05&limit=20
     * @apiSuccess {Array} Array of user trade info objects
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.user_id User id
     * @apiSuccess {Boolean} data.maker User is maker
     * @apiSuccess {Integer} data.order_id Order id
     * @apiSuccess {String} data.date_acceptance Order acceptance date
     * @apiSuccess {String} data.date_creation Order creation date
     * @apiSuccess {Number} data.amount Order amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.total Total sum
     * @apiSuccess {Number} data.commission commission
     * @apiSuccess {String} data.order_type Order type (BUY or SELL)
     */
    @GetMapping(value = "/history/{currency_pair}/trades", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<UserTradeHistoryDto>>> getUserTradeHistoryByCurrencyPair(@PathVariable(value = "currency_pair") String currencyPair,
                                                                                                     @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                                     @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                                     @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest().body(BaseResponse.error("From date is before to date"));
        }
        if (nonNull(limit) && limit <= 0) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Limit value equals or less than zero"));
        }

        final String transformedCurrencyPair = convert(currencyPair);

        return ResponseEntity.ok(BaseResponse.success(orderService.getUserTradeHistoryByCurrencyPair(transformedCurrencyPair, fromDate, toDate, limit)));
    }

    /**
     * @api {get} /openapi/v1/user/history/{order_id}/transactions Order transactions history
     * @apiName Order transactions history
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Provides collection of user transactions info objects
     * @apiParamExample Request Example:
     * openapi/v1/user/history/1/transactions
     * @apiSuccess {Array} Array of user trade info objects
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.transaction_id Transaction id
     * @apiSuccess {Integer} data.wallet_id User wallet id
     * @apiSuccess {Number} data.amount Amount to sell/buy
     * @apiSuccess {Number} data.commission Commission
     * @apiSuccess {String} data.currency Operation currency
     * @apiSuccess {String} data.time Transaction creation date
     * @apiSuccess {String} data.operation_type Transaction operation type
     * @apiSuccess {String} data.transaction_status Transaction status
     */
    @GetMapping(value = "/history/{order_id}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TransactionDto>>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderTransactions(orderId)));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
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
