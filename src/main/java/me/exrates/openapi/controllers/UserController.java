package me.exrates.openapi.controllers;

import me.exrates.openapi.exceptions.WrongDateOrderException;
import me.exrates.openapi.exceptions.WrongLimitException;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.dto.UserTradeHistoryDto;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionDto;
import me.exrates.openapi.models.dto.openAPI.OpenApiCommissionDto;
import me.exrates.openapi.models.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.models.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.services.OrderService;
import me.exrates.openapi.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static me.exrates.openapi.converters.CurrencyPairConverter.convert;
import static me.exrates.openapi.utils.ValidationUtil.validateLimit;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/user")
public class UserController {

    private final WalletService walletService;
    private final OrderService orderService;

    @Autowired
    public UserController(WalletService walletService,
                          OrderService orderService) {
        this.walletService = walletService;
        this.orderService = orderService;
    }

    /**
     * @apiDefine NonPublicAuth
     *  See Authentication API doc section
     */

    /**
     * @api {get} /openapi/v1/user/balances User Balances Info
     * @apiName User Balances Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of wallet objects
     * @apiParamExample Request Example:
     * /openapi/v1/user/balances
     * @apiSuccess {Array} Wallet objects result
     * @apiSuccess {Object} data                    Container object
     * @apiSuccess {String} data.currency_name      Name of currency
     * @apiSuccess {Number} data.active_balance     Balance that is available for spending
     * @apiSuccess {Number} data.reserved_balance   Balance reserved for orders or withdraw
     */
    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WalletBalanceDto>> getUserBalances() {
        return ResponseEntity.ok(walletService.getUserBalances());
    }

    /**
     * @api {get} /openapi/v1/user/orders/opened/{currency_1}/{currency_2}?limit User's opened orders by currency pair Info
     * @apiName Opened orders by currency pair Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user opened orders by currency pair
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/opened/btc/usd?limit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/opened/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserOpenOrdersByCurrencyPair(@PathVariable("currency_1") String currency1,
                                                                               @PathVariable("currency_2") String currency2,
                                                                               @RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(orderService.getUserOpenOrders(pairName, limit));
    }

    /**
     * @api {get} /openapi/v1/user/orders/opened?limit User's opened orders Info
     * @apiName Opened orders Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user opened orders
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/opened?limit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/opened", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserOpenOrders(@RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }
        return ResponseEntity.ok(orderService.getUserOpenOrders(null, limit));
    }

    /**
     * @api {get} /openapi/v1/user/orders/closed/{currency_1}/{currency_2}?limit User's closed orders by currency pair Info
     * @apiName Closed orders by currency pair Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user closed orders by currency pair
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/closed/btc/usd?limit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/closed/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserClosedOrdersByCurrencyPair(@PathVariable("currency_1") String currency1,
                                                                                 @PathVariable("currency_2") String currency2,
                                                                                 @RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(orderService.getUserClosedOrders(pairName, limit));
    }

    /**
     * @api {get} /openapi/v1/user/orders/closed?limit User's closed orders Info
     * @apiName Closed orders Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user closed orders
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/closedlimit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserClosedOrdersByCurrencyPair(@RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }
        return ResponseEntity.ok(orderService.getUserClosedOrders(null, limit));
    }

    /**
     * @api {get} /openapi/v1/user/orders/canceled/{currency_1}/{currency_2}?limit User's canceled orders by currency pair Info
     * @apiName Canceled orders by currency pair Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user canceled orders by currency pair
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/canceled/btc/usd?limit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/canceled/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> userCanceledOrders(@PathVariable("currency_1") String currency1,
                                                                  @PathVariable("currency_2") String currency2,
                                                                  @RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(orderService.getUserCanceledOrders(pairName, limit));
    }

    /**
     * @api {get} /openapi/v1/user/orders/canceled?limit User's canceled orders Info
     * @apiName Canceled orders Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of user canceled orders
     * @apiParam {Integer}      limit       limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/canceled?limit=20
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.currency_pair  Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.price          Exchange rate
     * @apiSuccess {String}     data.date_created   Creation time
     * @apiSuccess {String}     data.date_accepted  Acceptance time
     */
    @GetMapping(value = "/orders/canceled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> userCanceledOrders(@RequestParam(defaultValue = "50") Integer limit) {
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value should not be equals or less than zero");
        }
        return ResponseEntity.ok(orderService.getUserCanceledOrders(null, limit));
    }

    /**
     * @api {get} /openapi/v1/user/commissions User’s commission rates Info
     * @apiName User’s commission rates Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns info on user’s commission rates (as percent - for example, 0.5 rate means 0.5% of amount) by operation type.
     * Commissions for orders (sell and buy) are calculated and withdrawn from amount in quote currency.
     * @apiParamExample Request Example:
     * /openapi/v1/user/commissions
     * @apiSuccess {Object} data            Container object
     * @apiSuccess {Number} data.input      Commission for input operations
     * @apiSuccess {Number} data.output     Commission for output operations
     * @apiSuccess {Number} data.sell       Commission for sell operations
     * @apiSuccess {Number} data.buy        Commission for buy operations
     * @apiSuccess {Number} data.transfer   Commission for transfer operations
     */
    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpenApiCommissionDto> getCommissions() {
        CommissionDto allCommissions = orderService.getAllCommissions();

        return ResponseEntity.ok(new OpenApiCommissionDto(allCommissions));
    }

    /**
     * @api {get} /openapi/v1/user/history/trades/{currency_1}/{currency_2}?from_date&to_date&limit User trade history by currency pair Info
     * @apiName User Trade History by currency pair Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Provides array of user trade info objects
     * @apiParam {LocalDate}    from_date start date of search (date format: yyyy-MM-dd)
     * @apiParam {LocalDate}    to_date end date of search (date format: yyyy-MM-dd)
     * @apiParam {Integer}      limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
     * @apiParamExample Request Example:
     * openapi/v1/user/history/trades/btc/usd?from_date=2018-09-01&to_date=2018-09-05&limit=20
     * @apiSuccess {Array} Array of user trade info objects
     * @apiSuccess {Object}     data                    Container object
     * @apiSuccess {Integer}    data.user_id            User id
     * @apiSuccess {Boolean}    data.maker              User is maker
     * @apiSuccess {Integer}    data.order_id           Order id
     * @apiSuccess {String}     data.date_acceptance    Order acceptance date
     * @apiSuccess {String}     data.date_creation      Order creation date
     * @apiSuccess {Number}     data.amount             Order amount in base currency
     * @apiSuccess {Number}     data.price              Exchange rate
     * @apiSuccess {Number}     data.total              Total sum
     * @apiSuccess {Number}     data.commission         Commission
     * @apiSuccess {String}     data.order_type         Order type (BUY or SELL)
     */
    @GetMapping(value = "/history/trades/{currency_1}/{currency_2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserTradeHistoryDto>> getUserTradeHistoryByCurrencyPair(@PathVariable("currency_1") String currency1,
                                                                                       @PathVariable("currency_2") String currency2,
                                                                                       @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                       @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                       @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (fromDate.isAfter(toDate)) {
            throw new WrongDateOrderException("From date is before to date");
        }
        if (!validateLimit(limit)) {
            throw new WrongLimitException("Limit value equals or less than zero");
        }

        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(orderService.getUserTradeHistoryByCurrencyPair(pairName, fromDate, toDate, limit));
    }

    /**
     * @api {get} /openapi/v1/user/history/{order_id}/transactions Order transactions history Info
     * @apiName Order transactions history Info
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Provides array of user transactions info objects
     * @apiParamExample Request Example:
     * openapi/v1/user/history/1/transactions
     * @apiSuccess {Array} Array of user trade info objects
     * @apiSuccess {Object}     data                        Container object
     * @apiSuccess {Integer}    data.transaction_id         Transaction id
     * @apiSuccess {Integer}    data.wallet_id              User wallet id
     * @apiSuccess {Number}     data.amount                 Amount to sell/buy
     * @apiSuccess {Number}     data.commission             Commission
     * @apiSuccess {String}     data.currency               Operation currency
     * @apiSuccess {String}     data.time                   Transaction creation date
     * @apiSuccess {String}     data.operation_type         Transaction operation type
     * @apiSuccess {String}     data.transaction_status     Transaction status
     */
    @GetMapping(value = "/history/{order_id}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDto>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderTransactions(orderId));
    }
}
