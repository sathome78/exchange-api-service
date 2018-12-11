package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by Valk
 */
public class MyWalletsStatisticsApiDto {
    @JsonProperty(value = "id")
    private Integer walletId;
    private Integer userId;
    private Integer currencyId;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;


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

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
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

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyWalletsStatisticsApiDto that = (MyWalletsStatisticsApiDto) o;

        if (walletId != null ? !walletId.equals(that.walletId) : that.walletId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (currencyId != null ? !currencyId.equals(that.currencyId) : that.currencyId != null) return false;
        if (currencyName != null ? !currencyName.equals(that.currencyName) : that.currencyName != null) return false;
        if (activeBalance != null ? !activeBalance.equals(that.activeBalance) : that.activeBalance != null)
            return false;
        return reservedBalance != null ? reservedBalance.equals(that.reservedBalance) : that.reservedBalance == null;

    }

    @Override
    public int hashCode() {
        int result = walletId != null ? walletId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (currencyId != null ? currencyId.hashCode() : 0);
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MyWalletsStatisticsApiDto{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", currencyId=" + currencyId +
                ", currencyName='" + currencyName + '\'' +
                ", activeBalance=" + activeBalance +
                ", reservedBalance=" + reservedBalance +
                '}';
    }
}
