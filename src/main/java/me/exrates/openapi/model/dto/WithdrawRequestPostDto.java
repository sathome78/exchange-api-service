package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class WithdrawRequestPostDto {
  private int id;
  private String wallet;
  private String destinationTag;
  private String recipientBankName;
  private String recipientBankCode;
  private String userFullName;
  private String remark;
  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private WithdrawStatusEnum status;
  private String currencyName;
  private String merchantName;
  private String merchantServiceBeanName;
  private boolean withdrawTransferringConfirmNeeded;
  private Integer merchantId;
  private Integer currencyId;
  private Integer userId;
}
