package me.exrates.openapi.service.stopOrder;

import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.StopOrder;
import me.exrates.openapi.model.enums.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by maks on 20.04.2017.
 */
public interface StopOrderService {

    Integer createOrder(ExOrder exOrder);

    List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds);

    @Transactional
    boolean setStatus(int orderId, OrderStatus status);

}
