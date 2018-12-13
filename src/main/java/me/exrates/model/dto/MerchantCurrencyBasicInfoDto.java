package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MerchantCurrencyBasicInfoDto {
    private Integer merchantId;
    private String merchantName;
    private String currencyName;
    private Integer currencyId;
    private Integer refillScale;
    private Integer withdrawScale;
    private Integer transferScale;
}
