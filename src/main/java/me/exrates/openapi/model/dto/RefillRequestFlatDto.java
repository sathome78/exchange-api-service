package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.openapi.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter
@Setter
@ToString
public class RefillRequestFlatDto extends RequestWithRemarkAbstractDto {
  private int id;
  private String address;
  private String privKey;
  private String pubKey;
  private String brainPrivKey;
  private Integer userId;
  private String payerBankName;
  private String payerBankCode;
  private String payerAccount;
  private String userFullName;
  private String receiptScan;
  private String receiptScanName;
  private BigDecimal amount;
  private Integer commissionId;
  private RefillStatusEnum status;
  private LocalDateTime dateCreation;
  private LocalDateTime statusModificationDate;
  private Integer currencyId;
  private Integer merchantId;
  private String merchantTransactionId;
  private String recipientBankName;
  private Integer recipientBankId;
  private String recipientBankAccount;
  private String recipientBankRecipient;
  private String recipientBankDetails;
  private String merchantRequestSign;
  private Integer adminHolderId;
  private Integer refillRequestAddressId;
  private Integer refillRequestParamId;
  private InvoiceOperationPermission invoiceOperationPermission;

}
