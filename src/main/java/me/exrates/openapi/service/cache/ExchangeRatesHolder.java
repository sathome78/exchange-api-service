package me.exrates.openapi.service.cache;

import me.exrates.openapi.dao.OrderDao;
import me.exrates.openapi.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeRatesHolder {

    private Map<Integer, ExOrderStatisticsShortByPairsDto> ratesMap = new ConcurrentHashMap<>();

    private final OrderDao orderDao;

    @Autowired
    public ExchangeRatesHolder(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @PostConstruct
    private void init() {
        List<ExOrderStatisticsShortByPairsDto> list = orderDao.getOrderStatisticByPairs();
        list.forEach(p -> ratesMap.put(p.getCurrencyPairId(), p));
    }

    public void onRatesChange(Integer pairId, BigDecimal rate) {
        System.out.println("set holder rates");
        setRates(pairId, rate);
    }

    private synchronized void setRates(Integer pairId, BigDecimal rate) {
        if (ratesMap.containsKey(pairId)) {
            ExOrderStatisticsShortByPairsDto dto = ratesMap.get(pairId);
            dto.setPredLastOrderRate(dto.getLastOrderRate());
            dto.setLastOrderRate(rate.toPlainString());
        } else {
            ratesMap.put(pairId, orderDao.getOrderStatisticForSomePairs(Collections.singletonList(pairId)).get(0));
        }
    }

    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExOrderStatisticsShortByPairsDto> result = new ArrayList<>();
        id.forEach(p -> result.add(ratesMap.get(p)));
        return result;
    }
}
