package me.exrates.openapi.component;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.RefreshObjectsEnum;
import me.exrates.openapi.model.enums.UserRole;
import me.exrates.openapi.service.OrderService;
import me.exrates.openapi.service.UserService;
import me.exrates.openapi.service.cache.ChartsCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "ws_stomp_log")
@Component
public class StompMessenger {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DefaultSimpUserRegistry registry;
    @Autowired
    private ChartsCache chartsCache;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() -> registry.findSubscriptions(sub -> true)
                        .forEach(sub -> System.out.printf("sub: dest %s, user %s")),
                1, 2, TimeUnit.MINUTES);
    }

    public void sendChartData(final Integer currencyPairId, String resolution, String data) {
        log.error("send chart data to {} {}", currencyPairId, resolution);
        String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(resolution));
        sendMessageToDestination(destination, data);
    }

    public void sendChartData(final Integer currencyPairId) {
        Map<String, String> data = chartsCache.getData(currencyPairId);
        orderService.getIntervals().forEach(p -> {
            String message = data.get(p.getInterval());
            String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(p.getInterval()));
            sendMessageToDestination(destination, message);
        });
    }

    private void sendMessageToDestination(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendAlerts(final String message, final String lang) {
        log.debug("lang to send {}", lang);
        sendMessageToDestination("/app/users_alerts/".concat(lang), message);
    }

    public void sendEventMessage(final String sessionId, final String message) {
        sendMessageToDestination("/app/ev/".concat(sessionId), message);
    }

    public void sendStatisticMessage(List<Integer> currenciesIds) {
        Map<RefreshObjectsEnum, String> result = orderService.getSomeCurrencyStatForRefresh(currenciesIds);
        result.forEach((k, v) -> {
            sendMessageToDestination("/app/statistics/".concat(k.getSubscribeChannel()), v);
        });
    }

    public void sendMyTradesToUser(final int userId, final Integer currencyPair) {
        String userEmail = userService.getEmailById(userId);
        String destination = "/queue/personal/".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, userEmail, RefreshObjectsEnum.MY_TRADES);
        messagingTemplate.convertAndSendToUser(userEmail, destination, message);
    }

    public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType) {
        String message = orderService.getOrdersForRefresh(pairId, operationType, null);
        sendMessageToDestination("/app/trade_orders/".concat(pairId.toString()), message);
        sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);
    }

    private void sendRefreshTradeOrdersMessageToFiltered(Integer pairId, OperationType operationType) {
        Set<SimpSubscription> subscriptions =
                findSubscribersByDestination("/user/queue/trade_orders/f/".concat(pairId.toString()));
        if (!subscriptions.isEmpty()) {
            Map<UserRole, List<SimpSubscription>> map = new HashMap<>();
            subscriptions.forEach(p -> {
                String userEmail = p.getSession().getUser().getName();
                if (!StringUtils.isEmpty(userEmail)) {
                    UserRole role = userService.getUserRoleFromDB(userEmail);
                    if (map.containsKey(role)) {
                        map.get(role).add(p);
                    } else {
                        map.put(role, new ArrayList<SimpSubscription>() {{
                            add(p);
                        }});
                    }
                }
            });
            map.forEach((k, v) -> {
                String message = orderService.getOrdersForRefresh(pairId, operationType, k);
                for (SimpSubscription subscription : v) {
                    sendMessageToSubscription(subscription, message, "/queue/trade_orders/f/".concat(pairId.toString()));
                }
            });
        }
    }

    private Set<SimpSubscription> findSubscribersByDestination(final String destination) {
        return registry.findSubscriptions(subscription -> subscription.getDestination().equals(destination));
    }

    private void sendMessageToSubscription(SimpSubscription subscription, String message, String dest) {
        sendMessageToDestinationAndUser(subscription.getSession().getUser().getName(), dest, message);
    }

    private void sendMessageToDestinationAndUser(final String user, String destination, String message) {
        messagingTemplate.convertAndSendToUser(user,
                destination,
                message);
    }

    public void sendAllTrades(final Integer currencyPair) {
        String destination = "/app/trades/".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, null, RefreshObjectsEnum.ALL_TRADES);
        sendMessageToDestination(destination, message);
    }
}
