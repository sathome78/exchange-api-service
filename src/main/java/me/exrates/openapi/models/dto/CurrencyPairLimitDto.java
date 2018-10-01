package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPairLimitDto {

    private Integer currencyPairId;
    private String currencyPairName;
    private BigDecimal minRate;
    private BigDecimal maxRate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
