package me.exrates.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class BTCTransaction {

    private String hash;
    private BigDecimal amount;
    private int transactionId;
    private User acceptanceUser;
    private LocalDateTime acceptance_time;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public User getAcceptanceUser() {
        return acceptanceUser;
    }

    public void setAcceptanceUser(User acceptanceUser) {
        this.acceptanceUser = acceptanceUser;
    }

    public LocalDateTime getAcceptance_time() {
        return acceptance_time;
    }

    public void setAcceptance_time(LocalDateTime acceptance_time) {
        this.acceptance_time = acceptance_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTCTransaction that = (BTCTransaction) o;

        if (transactionId != that.transactionId) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (acceptanceUser != null ? !acceptanceUser.equals(that.acceptanceUser) : that.acceptanceUser != null)
            return false;
        return acceptance_time != null ? acceptance_time.equals(that.acceptance_time) : that.acceptance_time == null;

    }

    @Override
    public int hashCode() {
        int result = hash != null ? hash.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + transactionId;
        result = 31 * result + (acceptanceUser != null ? acceptanceUser.hashCode() : 0);
        result = 31 * result + (acceptance_time != null ? acceptance_time.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BTCTransaction{" +
                "hash='" + hash + '\'' +
                ", amount=" + amount +
                ", transactionId=" + transactionId +
                ", acceptanceUser=" + acceptanceUser +
                ", acceptance_time=" + acceptance_time +
                '}';
    }
}