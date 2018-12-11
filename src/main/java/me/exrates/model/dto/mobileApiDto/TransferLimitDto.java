package me.exrates.model.dto.mobileApiDto;

import java.math.BigDecimal;

/**
 * Created by OLEG on 13.02.2017.
 */
public class TransferLimitDto {
    private Integer currencyId;
    private BigDecimal transferMinLimit;

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getTransferMinLimit() {
        return transferMinLimit;
    }

    public void setTransferMinLimit(BigDecimal transferMinLimit) {
        this.transferMinLimit = transferMinLimit;
    }
}
