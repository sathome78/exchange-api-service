package me.exrates.model.dto.mobileApiDto.dashboard;

import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Valk on 14.04.16.
 */
public class OrderListApiDto {
    private int id;
    private int userId;
    private OperationType orderType;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;

    public OrderListApiDto(OrderListDto dto, Locale locale) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.orderType = dto.getOrderType();
        this.exrate = BigDecimalProcessing.parseLocale(dto.getExrate(), locale, 2);
        this.amountBase = BigDecimalProcessing.parseLocale(dto.getAmountBase(), locale, true);
        this.amountConvert = BigDecimalProcessing.parseLocale(dto.getAmountConvert(), locale, true);
    }

    /*hash*/

    @Override
    public int hashCode() {
        return id;
    }
    /*getters setters*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OperationType getOrderType() {
        return orderType;
    }

    public void setOrderType(OperationType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getExrate() {
        return exrate;
    }

    public void setExrate(BigDecimal exrate) {
        this.exrate = exrate;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(BigDecimal amountBase) {
        this.amountBase = amountBase;
    }

    public BigDecimal getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(BigDecimal amountConvert) {
        this.amountConvert = amountConvert;
    }
}
