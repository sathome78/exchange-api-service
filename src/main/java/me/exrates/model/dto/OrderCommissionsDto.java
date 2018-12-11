package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Created by Valk on 03.06.2016.
 */
public class OrderCommissionsDto {
    private BigDecimal sellCommission;
    private BigDecimal buyCommission;
    /*getters setters*/

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

    public static OrderCommissionsDto zeroComissions() {
        OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
        orderCommissionsDto.setSellCommission(BigDecimal.ZERO);
        orderCommissionsDto.setBuyCommission(BigDecimal.ZERO);
        return orderCommissionsDto;
    }
}
