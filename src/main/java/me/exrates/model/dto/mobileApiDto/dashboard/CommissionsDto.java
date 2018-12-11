package me.exrates.model.dto.mobileApiDto.dashboard;

import java.math.BigDecimal;

/**
 * Created by OLEG on 31.10.2016.
 */
public class CommissionsDto {

    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal sellCommission;
    private BigDecimal buyCommission;
    private BigDecimal transferCommission;

    public BigDecimal getInputCommission() {
        return inputCommission;
    }

    public void setInputCommission(BigDecimal inputCommission) {
        this.inputCommission = inputCommission;
    }

    public BigDecimal getOutputCommission() {
        return outputCommission;
    }

    public void setOutputCommission(BigDecimal ourputCommission) {
        this.outputCommission = ourputCommission;
    }

    public BigDecimal getSellCommission() {
        return sellCommission;
    }

    public void setSellCommission(BigDecimal sellCommission) {
        this.sellCommission = sellCommission;
    }

    public BigDecimal getBuyCommission() {
        return buyCommission;
    }

    public void setBuyCommission(BigDecimal buyCommission) {
        this.buyCommission = buyCommission;
    }

    public BigDecimal getTransferCommission() {
        return transferCommission;
    }

    public void setTransferCommission(BigDecimal transferCommission) {
        this.transferCommission = transferCommission;
    }

    @Override
    public String toString() {
        return "CommissionsDto{" +
                "inputCommission=" + inputCommission +
                ", outputCommission=" + outputCommission +
                ", sellCommission=" + sellCommission +
                ", buyCommission=" + buyCommission +
                ", transferCommission=" + transferCommission +
                '}';
    }
}
