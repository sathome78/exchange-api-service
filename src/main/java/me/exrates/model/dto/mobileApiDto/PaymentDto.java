package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 13.09.2016.
 */
public class PaymentDto {
    @NotNull
    private Integer currency;
    @NotNull
    private Integer merchant;
    @NotNull
    private Double sum;

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getMerchant() {
        return merchant;
    }

    public void setMerchant(Integer merchant) {
        this.merchant = merchant;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "PaymentDto{" +
                "currency=" + currency +
                ", merchant=" + merchant +
                ", sum=" + sum +
                '}';
    }
}
