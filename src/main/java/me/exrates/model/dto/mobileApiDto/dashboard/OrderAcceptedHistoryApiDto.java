package me.exrates.model.dto.mobileApiDto.dashboard;

import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;

/**
 * Created by Valk on 03.06.2016.
 */
public class OrderAcceptedHistoryApiDto {
    private Integer orderId;
    private Timestamp acceptionTime;
    private BigDecimal rate;
    private BigDecimal amountBase;
    private OperationType operationType;

    public OrderAcceptedHistoryApiDto(OrderAcceptedHistoryDto dto, Locale locale) {
        this.orderId = dto.getOrderId();
        this.acceptionTime = dto.getAcceptionTime();
        this.rate = BigDecimalProcessing.parseLocale(dto.getRate(), locale, true);
        this.amountBase = BigDecimalProcessing.parseLocale(dto.getAmountBase(), locale, true);;
        this.operationType = dto.getOperationType();
    }

    /*hash*/
    @Override
    public int hashCode() {
        return orderId != null ? orderId.hashCode() : 0;
    }
   /*getters setters*/

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(BigDecimal amountBase) {
        this.amountBase = amountBase;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Timestamp getAcceptionTime() {
        return acceptionTime;
    }

    public void setAcceptionTime(Timestamp acceptionTime) {
        this.acceptionTime = acceptionTime;
    }
}
