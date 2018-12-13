package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 16.02.2017.
 */
public class WithdrawInvoiceDto {
    @NotNull
    private Integer currency;
    @NotNull
    private Double sum;
    @NotNull
    private String walletNumber;
    @NotNull
    private String recipientBankName;
    private String recipientBankCode;
    @NotNull
    private String userFullName;
    private String remark;

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public String getRecipientBankName() {
        return recipientBankName;
    }

    public void setRecipientBankName(String recipientBankName) {
        this.recipientBankName = recipientBankName;
    }

    public String getRecipientBankCode() {
        return recipientBankCode;
    }

    public void setRecipientBankCode(String recipientBankCode) {
        this.recipientBankCode = recipientBankCode;
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
        return "WithdrawInvoiceDto{" +
                "currency=" + currency +
                ", sum=" + sum +
                ", walletNumber='" + walletNumber + '\'' +
                ", recipientBankName='" + recipientBankName + '\'' +
                ", recipientBankCode='" + recipientBankCode + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
