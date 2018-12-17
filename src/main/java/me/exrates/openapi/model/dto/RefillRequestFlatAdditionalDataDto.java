package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by ValkSam
 */
@Getter
@Setter
public class RefillRequestFlatAdditionalDataDto {
  private String userEmail;
  private String currencyName;
  private String merchantName;
  private String adminHolderEmail;
  private BigDecimal transactionAmount;
  private BigDecimal commissionAmount;
  private BigDecimal byBchAmount;
  private Integer confirmations;
}
