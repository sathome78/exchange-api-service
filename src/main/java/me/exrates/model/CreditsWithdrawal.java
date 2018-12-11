package me.exrates.model;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CreditsWithdrawal {

    private int userId;
    private int currency;
    private String meansOfPaymentId;
    private String meansOfPayment;
    private double sum;
    private int merchant;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getMeansOfPaymentId() {
        return meansOfPaymentId;
    }

    public void setMeansOfPaymentId(String meansOfPaymentId) {
        this.meansOfPaymentId = meansOfPaymentId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

    public String getMeansOfPayment() {
        return meansOfPayment;
    }

    public void setMeansOfPayment(String meansOfPayment) {
        this.meansOfPayment = meansOfPayment;
    }
}