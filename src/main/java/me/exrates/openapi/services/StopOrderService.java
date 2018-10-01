package me.exrates.openapi.services;


import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.StopOrder;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.repositories.StopOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StopOrderService {

    private final StopOrderDao stopOrderDao;

    @Autowired
    public StopOrderService(StopOrderDao stopOrderDao) {
        this.stopOrderDao = stopOrderDao;
    }

    //+
    @Transactional
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        return stopOrderDao.create(order);
    }

    //+
    @Transactional
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }
}
