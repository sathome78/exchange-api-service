package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by OLEG on 09.02.2017.
 */
public class InvoicePaymentDto {
    @NotNull
    private Integer currencyId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer bankId;
    @NotNull
    private String userFullName;
    private String remark;

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "InvoicePaymentDto{" +
                "currencyId=" + currencyId +
                ", amount=" + amount +
                ", bankId=" + bankId +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
