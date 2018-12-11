package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 13.09.2016.
 */
public class WithdrawDto {
    @NotNull
    private Integer currency;
    @NotNull
    private Integer merchant;
    @NotNull
    private Double sum;
    private int merchantImage;
    @NotNull
    private String destination;

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

    public int getMerchantImage() {
        return merchantImage;
    }

    public void setMerchantImage(int merchantImage) {
        this.merchantImage = merchantImage;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
