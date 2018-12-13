package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryOrdersDto {

    private String email;
    private String currencyName;
    private String role;
    private BigDecimal amountBuy;
    private BigDecimal amountBuyFee;
    private BigDecimal amountSell;
    private BigDecimal amountSellFee;

    public Boolean isEmpty() {
        return (isNull(amountBuy) || amountBuy.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(amountBuyFee) || amountBuyFee.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(amountSell) || amountSell.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(amountSellFee) || amountSellFee.compareTo(BigDecimal.ZERO) == 0);
    }
}
