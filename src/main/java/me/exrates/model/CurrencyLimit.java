package me.exrates.model;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by OLEG on 26.01.2017.
 */
public class CurrencyLimit {
    private Currency currency;
    private OperationType operationType;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer maxDailyRequest;
    private BigDecimal currencyUsdRate;
    private BigDecimal minSumUsdRate;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public BigDecimal getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(BigDecimal maxSum) {
        this.maxSum = maxSum;
    }

    public Integer getMaxDailyRequest() {
        return maxDailyRequest;
    }

    public void setMaxDailyRequest(Integer maxDailyRequest) {
        this.maxDailyRequest = maxDailyRequest;
    }

    public BigDecimal getCurrencyUsdRate() {
        return currencyUsdRate;
    }

    public void setCurrencyUsdRate(BigDecimal currencyUsdRate) {
        this.currencyUsdRate = currencyUsdRate;
    }

    public BigDecimal getMinSumUsdRate() {
        return minSumUsdRate;
    }

    public void setMinSumUsdRate(BigDecimal minSumUsdRate) {
        this.minSumUsdRate = minSumUsdRate;
    }
}
