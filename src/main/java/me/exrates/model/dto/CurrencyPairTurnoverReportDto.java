package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyPairTurnoverReportDto {

    private int currencyPairId;
    private String currencyPairName;
    private String currencyAccountingName;
    private Integer quantity;
    private BigDecimal amountConvert;
    private BigDecimal amountCommission;
}
