package me.exrates.model.dto.mobileApiDto;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by Valk
 */

public class OrderSummaryDto {
    private Integer currencyPairId;
    private OperationType operationType;
    private BigDecimal balance;
    private BigDecimal amount;
    private BigDecimal exrate;
    private BigDecimal total;
    private BigDecimal commission;
    private BigDecimal totalWithComission;
    private String key;

    /*constructors*/

    public OrderSummaryDto() {
    }

    public OrderSummaryDto(OrderCreateDto dto, String key) {
        this.currencyPairId = dto.getCurrencyPair().getId();
        this.operationType = dto.getOperationType();
        this.balance = dto.getSpentWalletBalance();
        this.amount = dto.getAmount();
        this.exrate = dto.getExchangeRate();
        this.total = dto.getTotal();
        this.commission = dto.getComission();
        this.totalWithComission = dto.getTotalWithComission();
        this.key = key;
    }

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairName) {
        this.currencyPairId = currencyPairName;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getExrate() {
        return exrate;
    }

    public void setExrate(BigDecimal exrate) {
        this.exrate = exrate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getTotalWithComission() {
        return totalWithComission;
    }

    public void setTotalWithComission(BigDecimal totalWithComission) {
        this.totalWithComission = totalWithComission;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
