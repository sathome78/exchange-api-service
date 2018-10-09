package me.exrates.openapi.controllers;

import me.exrates.openapi.aspect.AccessCheck;
import me.exrates.openapi.aspect.RateLimitCheck;
import me.exrates.openapi.models.dto.CandleChartItemReducedDto;
import me.exrates.openapi.models.dto.TradeHistoryDto;
import me.exrates.openapi.models.dto.CurrencyPairInfoItem;
import me.exrates.openapi.models.dto.OrderBookItem;
import me.exrates.openapi.models.dto.TickerDto;
import me.exrates.openapi.models.enums.IntervalType;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.vo.BackDealInterval;
import me.exrates.openapi.services.CurrencyService;
import me.exrates.openapi.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.openapi.utils.ValidationUtil.validateCurrencyPair;
import static me.exrates.openapi.utils.ValidationUtil.validateDate;
import static me.exrates.openapi.utils.ValidationUtil.validateLimit;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final OrderService orderService;
    private final CurrencyService currencyService;

    @Autowired
    public PublicController(OrderService orderService,
                            CurrencyService currencyService) {
        this.orderService = orderService;
        this.currencyService = currencyService;
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/ticker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TickerDto>> getTicker(@RequestParam(value = "currency_pair", required = false) String pair) {
        if (nonNull(pair)) {
            validateCurrencyPair(pair);
        }
        List<TickerDto> result = orderService.getDailyCoinmarketData(pair).stream()
                .map(TickerDto::new)
                .collect(toList());

        return ResponseEntity.ok(result);
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/order_book", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<OrderType, List<OrderBookItem>>> getOrderBook(@RequestParam(value = "currency_pair") String pair,
                                                                            @RequestParam(value = "order_type", required = false) OrderType orderType,
                                                                            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        validateCurrencyPair(pair);

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getOrderBook(pair, orderType, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TradeHistoryDto>> getTradeHistoryByCurrencyPair(@RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                               @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                               @RequestParam(value = "currency_pair") String pair,
                                                                               @RequestParam(defaultValue = "50", required = false) Integer limit) {
        validateDate(fromDate, toDate);

        validateCurrencyPair(pair);

        validateLimit(limit);

        return ResponseEntity.ok(orderService.getTradeHistory(pair, fromDate, toDate, limit));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/currency_pairs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CurrencyPairInfoItem>> findActiveCurrencyPairs() {
        return ResponseEntity.ok(currencyService.getActiveCurrencyPairs());
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/candle_chart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CandleChartItemReducedDto>> getCandleChartData(@RequestParam(value = "currency_pair") String pair,
                                                                              @RequestParam(value = "interval_type") IntervalType intervalType,
                                                                              @RequestParam(value = "interval_value") Integer intervalValue) {
        validateCurrencyPair(pair);

        List<CandleChartItemReducedDto> resultList = orderService.getDataForCandleChart(pair, new BackDealInterval(intervalValue, intervalType)).stream()
                .map(CandleChartItemReducedDto::new)
                .collect(toList());
        return ResponseEntity.ok(resultList);
    }
}
