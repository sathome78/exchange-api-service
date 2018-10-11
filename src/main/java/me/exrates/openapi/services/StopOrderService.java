package me.exrates.openapi.services;


import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.StopOrder;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.repositories.StopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StopOrderService {

    private final StopOrderRepository stopOrderRepository;

    @Autowired
    public StopOrderService(StopOrderRepository stopOrderRepository) {
        this.stopOrderRepository = stopOrderRepository;
    }

    @Loggable(caption = "Set stop order status")
    @Transactional
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderRepository.setStatus(orderId, status);
    }

    @Loggable(caption = "Create stop order")
    @Transactional
    public Integer createOrder(ExOrder order) {
        StopOrder stopOrder = new StopOrder(order);
        return stopOrderRepository.create(stopOrder);
    }
}
