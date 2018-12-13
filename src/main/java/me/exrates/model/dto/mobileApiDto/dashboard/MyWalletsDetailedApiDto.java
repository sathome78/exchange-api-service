package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Valk
 */
public class MyWalletsDetailedApiDto {

    @JsonProperty(value = "id")
    private Integer walletId;
    private Integer userId;
    private Integer currencyId;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal onConfirmation;
    private BigDecimal onConfirmationStage;
    private BigDecimal onConfirmationCount;
    private BigDecimal reservedBalance;
    private BigDecimal reservedByOrders;
    private BigDecimal reservedByMerchant;

    public MyWalletsDetailedApiDto(MyWalletsDetailedDto dto, Locale locale) {
        this.walletId = dto.getId();
        this.userId = dto.getUserId();
        this.currencyId = dto.getCurrencyId();
        this.currencyName = dto.getCurrencyName();
        this.activeBalance = BigDecimalProcessing.parseLocale(dto.getActiveBalance() ,locale, 2);
        this.onConfirmation = BigDecimalProcessing.parseLocale(dto.getOnConfirmation() ,locale, 2);
        this.onConfirmationStage = BigDecimalProcessing.parseLocale(dto.getOnConfirmationStage() ,locale, 0);
        this.onConfirmationCount = BigDecimalProcessing.parseLocale(dto.getOnConfirmationCount() ,locale, 0);
        this.reservedBalance = BigDecimalProcessing.parseLocale(dto.getReservedBalance() ,locale, 2);
        this.reservedByOrders = BigDecimalProcessing.parseLocale(dto.getReservedByOrders() ,locale, 2);
        this.reservedByMerchant = BigDecimalProcessing.parseLocale(dto.getReservedByMerchant() ,locale, 2);
    }

    /*hash*/



    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getActiveBalance() {
        return activeBalance;
    }

    public void setActiveBalance(BigDecimal activeBalance) {
        this.activeBalance = activeBalance;
    }

    public BigDecimal getOnConfirmation() {
        return onConfirmation;
    }

    public void setOnConfirmation(BigDecimal onConfirmation) {
        this.onConfirmation = onConfirmation;
    }

    public BigDecimal getOnConfirmationStage() {
        return onConfirmationStage;
    }

    public void setOnConfirmationStage(BigDecimal onConfirmationStage) {
        this.onConfirmationStage = onConfirmationStage;
    }

    public BigDecimal getOnConfirmationCount() {
        return onConfirmationCount;
    }

    public void setOnConfirmationCount(BigDecimal onConfirmationCount) {
        this.onConfirmationCount = onConfirmationCount;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    public BigDecimal getReservedByOrders() {
        return reservedByOrders;
    }

    public void setReservedByOrders(BigDecimal reservedByOrders) {
        this.reservedByOrders = reservedByOrders;
    }

    public BigDecimal getReservedByMerchant() {
        return reservedByMerchant;
    }

    public void setReservedByMerchant(BigDecimal reservedByMerchant) {
        this.reservedByMerchant = reservedByMerchant;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String toString() {
        return "MyWalletsDetailedApiDto{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", currencyId=" + currencyId +
                ", currencyName='" + currencyName + '\'' +
                ", activeBalance=" + activeBalance +
                ", onConfirmation=" + onConfirmation +
                ", onConfirmationStage=" + onConfirmationStage +
                ", onConfirmationCount=" + onConfirmationCount +
                ", reservedBalance=" + reservedBalance +
                ", reservedByOrders=" + reservedByOrders +
                ", reservedByMerchant=" + reservedByMerchant +
                '}';
    }
}
