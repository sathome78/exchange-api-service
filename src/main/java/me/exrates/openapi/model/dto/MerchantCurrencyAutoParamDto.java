package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by ValkSam
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class MerchantCurrencyAutoParamDto {
  private Boolean withdrawAutoEnabled;
  private Integer withdrawAutoDelaySeconds;
  private BigDecimal withdrawAutoThresholdAmount;
}
