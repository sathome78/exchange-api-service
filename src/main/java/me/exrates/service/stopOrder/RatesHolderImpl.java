package me.exrates.service.stopOrder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Created by maks on 22.04.2017.
 * rates map holds rate of the last deal by the each currency
 * if/ there no deals was by the currency getCurrentRate will return null;
 *
 * Now it holds the same rates for buy and sale;
 *
 */
@Log4j2
@Component
public class RatesHolderImpl implements RatesHolder {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private OrderService orderService;

    /*contains currency pairId and its rate*/
    private Table<Integer, OperationType, BigDecimal> ratesMap = HashBasedTable.create();

    @PostConstruct
    public void init() {

    }

    @Override
    public void onRateChange(int pairId, BigDecimal rate) {
        ratesMap.put(pairId, OperationType.BUY, rate);
        ratesMap.put(pairId, OperationType.SELL, rate);
    }

    @Override
    public BigDecimal getCurrentRate(int pairId, OperationType operationType) {
        return ratesMap.get(pairId, operationType);
    }



}
