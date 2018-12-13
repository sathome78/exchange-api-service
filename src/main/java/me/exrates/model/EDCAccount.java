package me.exrates.model;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class EDCAccount {

    private int transactionId;
    private String wifPrivKey;
    private String pubKey;
    private String brainPrivKey;
    private String accountId;
    private String accountName;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getWifPrivKey() {
        return wifPrivKey;
    }

    public void setWifPrivKey(String wifPrivKey) {
        this.wifPrivKey = wifPrivKey;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getBrainPrivKey() {
        return brainPrivKey;
    }

    public void setBrainPrivKey(String brainPrivKey) {
        this.brainPrivKey = brainPrivKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EDCAccount that = (EDCAccount) o;

        if (transactionId != that.transactionId) return false;
        if (wifPrivKey != null ? !wifPrivKey.equals(that.wifPrivKey) : that.wifPrivKey != null) return false;
        if (pubKey != null ? !pubKey.equals(that.pubKey) : that.pubKey != null) return false;
        if (brainPrivKey != null ? !brainPrivKey.equals(that.brainPrivKey) : that.brainPrivKey != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        return accountName != null ? accountName.equals(that.accountName) : that.accountName == null;

    }

    @Override
    public int hashCode() {
        int result = transactionId;
        result = 31 * result + (wifPrivKey != null ? wifPrivKey.hashCode() : 0);
        result = 31 * result + (pubKey != null ? pubKey.hashCode() : 0);
        result = 31 * result + (brainPrivKey != null ? brainPrivKey.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EDCAccount{" +
                "transactionId=" + transactionId +
                ", wifPrivKey='" + wifPrivKey + '\'' +
                ", pubKey='" + pubKey + '\'' +
                ", brainPrivKey='" + brainPrivKey + '\'' +
                ", accountId='" + accountId + '\'' +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
