package me.exrates.openapi.service.stopOrder;


import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.StopOrderDao;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.StopOrder;
import me.exrates.openapi.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by maks on 20.04.2017.
 */

@Service
@Log4j2
public class StopOrderServiceImpl implements StopOrderService {

    @Autowired
    private StopOrderDao stopOrderDao;
    @Autowired
    private StopOrdersHolder stopOrdersHolder;
    @Autowired
    private RatesHolder ratesHolder;

    private final static int THREADS_NUMBER = 50;
    private final static ExecutorService checkExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> buyLocks = new ConcurrentHashMap<>();
    private final static ExecutorService ordersExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> sellLocks = new ConcurrentHashMap<>();


    @Transactional
    @Override
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        return stopOrderDao.create(order);
    }



    @Override
    public List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds) {
        return stopOrderDao.getOrdersBypairId(pairIds, OrderStatus.OPENED);
    }


    @Transactional
    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }




    @PreDestroy
    private void shutdown() {
        checkExecutors.shutdown();
        ordersExecutors.shutdown();
    }
}
