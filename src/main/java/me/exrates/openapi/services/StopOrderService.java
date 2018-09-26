package me.exrates.openapi.services;


import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.repositories.StopOrderDao;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.StopOrder;
import me.exrates.openapi.models.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class StopOrderService {

    @Autowired
    private StopOrderDao stopOrderDao;

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
