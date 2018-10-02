package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.invoice.RefillStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class RefillRequest {

    private int id;
    private String address;
    private Integer userId;
    private String payerBankName;
    private String payerBankCode;
    private String payerAccount;
    private String userFullName;
    private String remark;
    private String receiptScan;
    private String receiptScanName;
    private BigDecimal amount;
    private Integer commissionId;
    private RefillStatus status;
    private LocalDateTime dateCreation;
    private LocalDateTime statusModificationDate;
    private Integer currencyId;
    private Integer merchantId;
    private String merchantTransactionId;
    private String recipientBankName;
    private Integer recipientBankId;
    private String recipientBankAccount;
    private String recipientBankRecipient;
    private Integer adminHolderId;
    private Integer confirmations;
}
