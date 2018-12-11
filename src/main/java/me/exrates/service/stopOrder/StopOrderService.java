package me.exrates.service.stopOrder;

import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.enums.OrderStatus;
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

    /*@Transactional
    OrderInfoDto getStopOrderInfo(int orderId, Locale locale);*/

}
