package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 01.03.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditMerchantCommissionDto {
    private Integer merchantId;
    private Integer currencyId;
    private BigDecimal inputValue;
    private BigDecimal outputValue;
    private BigDecimal transferValue;
    private BigDecimal minFixedAmount;
}
