package me.exrates.openapi.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.exrates.openapi.aspect.AccessCheck;
import me.exrates.openapi.aspect.RateLimitCheck;
import me.exrates.openapi.models.dto.CandleChartItemReducedDto;
import me.exrates.openapi.models.dto.CurrencyPairInfoItem;
import me.exrates.openapi.models.dto.OrderBookDto;
import me.exrates.openapi.models.dto.OrderBookItemDto;
import me.exrates.openapi.models.dto.TickerDto;
import me.exrates.openapi.models.dto.TickerItemDto;
import me.exrates.openapi.models.dto.TradeHistoryDto;
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

@Api(
        value = "Public API",
        description = "Public API operations"
)
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
    @ApiOperation(value = "Get ticker information by currency pair (get information about all tickers, if you leave the pair field blank)", position = 1)
    @GetMapping(value = "/ticker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TickerDto> getTicker(@ApiParam(value = "Currency pair", example = "BTC/USD") @RequestParam(value = "currency_pair", required = false) String pair) {
        if (nonNull(pair)) {
            pair = pair.toUpperCase();

            validateCurrencyPair(pair);
        }
        List<TickerItemDto> result = orderService.getDailyCoinmarketData(pair).stream()
                .map(TickerItemDto::new)
                .collect(toList());

        return ResponseEntity.ok(new TickerDto(result));
    }

    @AccessCheck
    @RateLimitCheck
    @ApiOperation(value = "Get order book information by currency pair and order type", position = 2)
    @GetMapping(value = "/order_book", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderBookDto> getOrderBook(@ApiParam(value = "Currency pair", example = "BTC/USD", required = true) @RequestParam(value = "currency_pair") String pair,
                                                     @ApiParam(value = "Order type", example = "BUY") @RequestParam(value = "order_type", required = false) OrderType orderType,
                                                     @ApiParam(value = "Limit", example = "50") @RequestParam(required = false, defaultValue = "50") Integer limit) {
        pair = pair.toUpperCase();

        validateCurrencyPair(pair);
        validateLimit(limit);

        Map<OrderType, List<OrderBookItemDto>> result = orderService.getOrderBook(pair, orderType, limit);
        return ResponseEntity.ok(new OrderBookDto(result));
    }

    @AccessCheck
    @RateLimitCheck
    @ApiOperation(value = "Get history information by currency pair", position = 3)
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TradeHistoryDto>> getTradeHistoryByCurrencyPair(@ApiParam(value = "From date", example = "2018-10-01", required = true) @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                               @ApiParam(value = "To date", example = "2018-10-02", required = true) @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                               @ApiParam(value = "Currency pair", example = "BTC/USD", required = true) @RequestParam(value = "currency_pair") String pair,
                                                                               @RequestParam(defaultValue = "50", required = false) Integer limit) {
        pair = pair.toUpperCase();

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
        pair = pair.toUpperCase();

        validateCurrencyPair(pair);

        List<CandleChartItemReducedDto> resultList = orderService.getDataForCandleChart(pair, new BackDealInterval(intervalValue, intervalType)).stream()
                .map(CandleChartItemReducedDto::new)
                .collect(toList());
        return ResponseEntity.ok(resultList);
    }
}
