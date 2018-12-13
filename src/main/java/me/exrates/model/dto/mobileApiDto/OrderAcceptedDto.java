package me.exrates.model.dto.mobileApiDto;

import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.OperationType;

import java.sql.Timestamp;

/**
 * Created by OLEG on 05.09.2016.
 */
public class OrderAcceptedDto {
    private Integer orderId;
    private Timestamp acceptionTime;
    private String rate;
    private String amountBase;
    private OperationType operationType;

    public OrderAcceptedDto(OrderAcceptedHistoryDto orderAcceptedHistoryDto) {
        this.orderId = orderAcceptedHistoryDto.getOrderId();
        this.acceptionTime = orderAcceptedHistoryDto.getAcceptionTime();
        this.rate = orderAcceptedHistoryDto.getRate();
        this.amountBase = orderAcceptedHistoryDto.getAmountBase();
        this.operationType = orderAcceptedHistoryDto.getOperationType();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Timestamp getAcceptionTime() {
        return acceptionTime;
    }

    public void setAcceptionTime(Timestamp acceptionTime) {
        this.acceptionTime = acceptionTime;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(String amountBase) {
        this.amountBase = amountBase;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
}
