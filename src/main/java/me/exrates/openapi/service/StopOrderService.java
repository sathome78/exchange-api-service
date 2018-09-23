package me.exrates.openapi.service;


import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.StopOrderDao;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.StopOrder;
import me.exrates.openapi.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class StopOrderService {

    @Autowired
    private StopOrderDao stopOrderDao;

    @Transactional
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        return stopOrderDao.create(order);
    }

    @Transactional
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }
}
