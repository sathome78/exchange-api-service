package me.exrates.model;

import java.math.BigInteger;

public class EthereumAccount {

    private String address;
    private User user;
    private BigInteger privateKey;
    private BigInteger publicKey;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(BigInteger privateKey) {
        this.privateKey = privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(BigInteger publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EthereumAccount that = (EthereumAccount) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        return publicKey != null ? publicKey.equals(that.publicKey) : that.publicKey == null;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EthereumAccount{" +
                "address='" + address + '\'' +
                ", user=" + user +
                ", privateKey=" + privateKey +
                ", publicKey=" + publicKey +
                '}';
    }
}
