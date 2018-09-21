package me.exrates.openapi.service.stopOrder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * rates map holds rate of the last deal by the each currency
 * if/ there no deals was by the currency getCurrentRate will return null;
 * <p>
 * Now it holds the same rates for buy and sale;
 */
@Log4j2
@Component
public class RatesHolder {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private OrderService orderService;

    /*contains currency pairId and its rate*/
    private Table<Integer, OperationType, BigDecimal> ratesMap = HashBasedTable.create();

    @PostConstruct
    public void init() {

    }

    public void onRateChange(int pairId, OperationType operationType, BigDecimal rate) {
        ratesMap.put(pairId, OperationType.BUY, rate);
        ratesMap.put(pairId, OperationType.SELL, rate);
    }

    public BigDecimal getCurrentRate(int pairId, OperationType operationType) {
        return ratesMap.get(pairId, operationType);
    }
}
