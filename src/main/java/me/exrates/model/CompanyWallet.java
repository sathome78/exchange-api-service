package me.exrates.model;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CompanyWallet {

    private int id;
    private Currency currency;
    private BigDecimal balance;
    private BigDecimal commissionBalance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCommissionBalance() {
        return commissionBalance;
    }

    public void setCommissionBalance(BigDecimal commissionBalance) {
        this.commissionBalance = commissionBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyWallet that = (CompanyWallet) o;

        if (id != that.id) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        return commissionBalance != null ? commissionBalance.equals(that.commissionBalance) : that.commissionBalance == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (commissionBalance != null ? commissionBalance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompanyWallet{" +
                "id=" + id +
                ", currency=" + currency +
                ", balance=" + balance +
                ", commissionBalance=" + commissionBalance +
                '}';
    }
}