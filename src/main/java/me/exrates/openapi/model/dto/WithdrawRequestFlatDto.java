package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class WithdrawRequestFlatDto {
  private int id;
  private String wallet;
  private String destinationTag;
  private Integer userId;
  private String recipientBankName;
  private String recipientBankCode;
  private String userFullName;
  private String remark;
  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private BigDecimal merchantCommissionAmount;
  private Integer commissionId;
  private WithdrawStatusEnum status;
  private LocalDateTime dateCreation;
  private LocalDateTime statusModificationDate;
  private Integer currencyId;
  private Integer merchantId;
  private Integer adminHolderId;
  private InvoiceOperationPermission invoiceOperationPermission;
  private String transactionHash;
  private String additionalParams;
}
