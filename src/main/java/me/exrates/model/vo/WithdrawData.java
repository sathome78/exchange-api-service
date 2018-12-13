package me.exrates.model.vo;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 07.02.2017.
 */
public class WithdrawData {
    @NotNull
    private String recipientBankName;
    private String recipientBankCode;
    @NotNull
    private String userAccount;
    private String userFullName;
    private String remark;


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

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
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
        return "InvoiceConfirmData{" +
                ", recipientBankName='" + recipientBankName + '\'' +
                ", recipientBankCode='" + recipientBankCode + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
