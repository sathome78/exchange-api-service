package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exrates.model.Commission;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter
@ToString
@AllArgsConstructor
public class CommissionDataDto {
  BigDecimal amount;
  /**/
  BigDecimal merchantCommissionRate;
  BigDecimal minMerchantCommissionAmount;
  String merchantCommissionUnit;
  BigDecimal merchantCommissionAmount;
  /**/
  Commission companyCommission;
  BigDecimal companyCommissionRate;
  String companyCommissionUnit;
  BigDecimal companyCommissionAmount;
  /**/
  BigDecimal totalCommissionAmount;
  BigDecimal resultAmount;

  Boolean specificMerchantComissionCount;

}
