package me.exrates.openapi.service.handler;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.service.cache.ExchangeRatesHolder;
import me.exrates.openapi.service.events.AcceptOrderEvent;
import me.exrates.openapi.service.events.CreateOrderEvent;
import me.exrates.openapi.service.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class OrdersEventHandleService {

    @Autowired
    private ExchangeRatesHolder ratesHolder;
    @Autowired
    private CurrencyStatisticsHandler currencyStatisticsHandler;


    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private Map<Integer, TradesEventsHandler> mapTrades = new ConcurrentHashMap<>();
    private Map<Integer, MyTradesHandler> mapMyTrades = new ConcurrentHashMap<>();
    private Map<Integer, ChartRefreshHandler> mapChart = new ConcurrentHashMap<>();


    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(OrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        log.debug("order event {} ", exOrder);
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(CreateOrderEvent event) {
        log.debug("new thr create {} ", Thread.currentThread().getName());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(AcceptOrderEvent event) {
        log.debug("new thr accept {} ", Thread.currentThread().getName());
        ExOrder order = (ExOrder) event.getSource();
        handleAllTrades(order);
        handleMyTrades(order);
        handleChart(order);
        ratesHolder.onRatesChange(order.getCurrencyPairId(), order.getExRate());
        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
    }


    private void onOrdersEvent(Integer pairId, OperationType operationType) {
        Map<Integer, OrdersEventsHandler> mapForWork;
        if (operationType.equals(OperationType.BUY)) {
            mapForWork = mapBuy;
        } else if (operationType.equals(OperationType.SELL)) {
            mapForWork = mapSell;
        } else {
            log.error("no such map");
            return;
        }
        OrdersEventsHandler handler = mapForWork
                .computeIfAbsent(pairId, k -> OrdersEventsHandler.init(pairId, operationType));
        handler.onOrderEvent();
    }

    @Async
    void handleAllTrades(ExOrder exOrder) {
        TradesEventsHandler handler = mapTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> TradesEventsHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }

    @Async
    void handleMyTrades(ExOrder exOrder) {
        MyTradesHandler handler = mapMyTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> MyTradesHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent(exOrder.getUserId());
        handler.onAcceptOrderEvent(exOrder.getUserAcceptorId());
    }

    @Async
    void handleChart(ExOrder exOrder) {
        ChartRefreshHandler handler = mapChart
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> ChartRefreshHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }
}
