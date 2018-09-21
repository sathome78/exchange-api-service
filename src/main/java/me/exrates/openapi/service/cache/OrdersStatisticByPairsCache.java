package me.exrates.openapi.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.openapi.dao.CurrencyDao;
import me.exrates.openapi.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2(topic = "cache")
@Component
public class OrdersStatisticByPairsCache {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private CurrencyDao currencyDao;

    private List<CurrencyPair> allPairs = new ArrayList<>();

    private List<ExOrderStatisticsShortByPairsDto> allStatsCachedList = new CopyOnWriteArrayList<>();

    private List<ExOrderStatisticsShortByPairsDto> cachedList = new CopyOnWriteArrayList<>();

    private Semaphore semaphore = new Semaphore(1, true);

    private CyclicBarrier barrier = new CyclicBarrier(1000);

    private AtomicBoolean needUpdate = new AtomicBoolean(true);

   /* @PostConstruct
    private void init() {
        allPairs = currencyDao.getAllCurrencyPairs(CurrencyPairType.ALL);
        update();
        needUpdate.set(false);
        log.info("initialized, {}", cachedList.size());
    }*/

    public void update() {
        if (semaphore.tryAcquire()) {
            updateCache();
        }
    }

    private void updateCache() {
        try {
            log.info("try update cache");
            log.info("update cache");
            this.cachedList = orderDao.getOrderStatisticByPairs();
            needUpdate.set(false);
        } finally {
            semaphore.release();
        }
    }

    public List<ExOrderStatisticsShortByPairsDto> getAllPairsCahedList() {
        log.info("get cache");
        checkCache();
        return allStatsCachedList;
    }

/*    public List<ExOrderStatisticsShortByPairsDto> getCachedList() {
        log.info("get cache");
        checkCache();
        return cachedList;
    }*/

    public void setNeedUpdate(boolean newNeedUpdate) {
        needUpdate.set(newNeedUpdate);
    }

    public List<ExOrderStatisticsShortByPairsDto> getCachedList() {
        log.info("get cache");
        if (needUpdate.get()) {
            if (semaphore.tryAcquire()) {
                updateCache();
                barrier.reset();
            } else {
                try {
                    barrier.await(4, TimeUnit.SECONDS);
                } catch (Exception e) {
                    barrier.reset();
                }
                /*LocalTime startTime = LocalTime.now();
                while (needUpdate.get()) {
                    if (startTime.until(LocalDateTime.now(),  ChronoUnit.SECONDS) > 10) {
                        break;
                    }
                }*/
            }
        }
        return cachedList;
    }

    private void checkCache() {
        if (needUpdate.get()) {
            if (semaphore.tryAcquire()) {
                updateCache();
                barrier.reset();
            } else {
                try {
                    barrier.await(4, TimeUnit.SECONDS);
                } catch (Exception e) {
                    barrier.reset();
                }
            }
        }
    }

    private void updateAllPairs() {
        allStatsCachedList.clear();
        allStatsCachedList.addAll(this.cachedList);
        allPairs.forEach(pair -> {
            boolean available = false;
            for (ExOrderStatisticsShortByPairsDto order : this.cachedList) {
                if (order.getCurrencyPairName().equals(pair.getName())) {
                    available = true;
                    break;
                }
            }
            if (!available) {
                ExOrderStatisticsShortByPairsDto dto = new ExOrderStatisticsShortByPairsDto();
                dto.setCurrencyPairName(pair.getName());
                dto.setNeedRefresh(true);
                dto.setLastOrderRate(BigDecimal.ZERO.toPlainString());
                dto.setPredLastOrderRate(BigDecimal.ZERO.toPlainString());
                dto.setPercentChange(BigDecimal.ZERO.toPlainString());
                allStatsCachedList.add(dto);
            }
        });
    }
}
