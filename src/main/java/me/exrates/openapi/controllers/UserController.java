package me.exrates.openapi.controllers;

import me.exrates.openapi.aspect.AccessCheck;
import me.exrates.openapi.aspect.RateLimitCheck;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.dto.UserTradeHistoryDto;
import me.exrates.openapi.models.dto.CommissionDto;
import me.exrates.openapi.models.dto.OpenApiCommissionDto;
import me.exrates.openapi.models.dto.UserOrdersDto;
import me.exrates.openapi.models.dto.WalletBalanceDto;
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

import static java.util.Objects.nonNull;
import static me.exrates.openapi.utils.ValidationUtil.validateCurrencyPair;
import static me.exrates.openapi.utils.ValidationUtil.validateDate;
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

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WalletBalanceDto>> getUserBalances() {
        return ResponseEntity.ok(walletService.getUserBalances());
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/orders/opened", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserOpenOrdersByCurrencyPair(@RequestParam(value = "currency_pair", required = false) String pair,
                                                                               @RequestParam(defaultValue = "50") Integer limit) {
        if (nonNull(pair)) {
            validateCurrencyPair(pair);
        }

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getUserOpenOrders(pair, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getUserClosedOrdersByCurrencyPair(@RequestParam(value = "currency_pair", required = false) String pair,
                                                                                 @RequestParam(defaultValue = "50") Integer limit) {
        if (nonNull(pair)) {
            validateCurrencyPair(pair);
        }

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getUserClosedOrders(pair, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/orders/canceled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> userCanceledOrders(@RequestParam(value = "currency_pair", required = false) String pair,
                                                                  @RequestParam(defaultValue = "50") Integer limit) {
        if (nonNull(pair)) {
            validateCurrencyPair(pair);
        }

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getUserCanceledOrders(pair, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpenApiCommissionDto> getCommissions() {
        CommissionDto allCommissions = orderService.getAllCommissions();

        return ResponseEntity.ok(new OpenApiCommissionDto(allCommissions));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/history/trades", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserTradeHistoryDto>> getUserTradeHistoryByCurrencyPair(@RequestParam(value = "currency_pair") String pair,
                                                                                       @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                       @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                       @RequestParam(required = false, defaultValue = "50") Integer limit) {
        validateDate(fromDate, toDate);

        validateCurrencyPair(pair);

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getUserTradeHistoryByCurrencyPair(pair, fromDate, toDate, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/history/{order_id}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDto>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderTransactions(orderId));
    }
}
