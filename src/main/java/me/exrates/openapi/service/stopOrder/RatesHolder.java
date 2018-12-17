package me.exrates.openapi.service.stopOrder;

import me.exrates.openapi.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by maks on 22.04.2017.
 */
public interface RatesHolder {

    void onRateChange(int pairId, BigDecimal rate);

    BigDecimal getCurrentRate(int pairId, OperationType operationType);
}
