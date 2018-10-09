package me.exrates.openapi.models;

import lombok.*;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"userWallet", "companyWallet", "commission", "currency", "merchant", "order", "withdrawRequest", "refillRequest"})
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private int id;
    private Wallet userWallet;
    private CompanyWallet companyWallet;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Commission commission;
    private OperationType operationType;
    private Currency currency;
    private Merchant merchant;
    private LocalDateTime datetime;
    private ExOrder order;
    private boolean provided;
    private Integer confirmation;
    private BigDecimal activeBalanceBefore;
    private BigDecimal reservedBalanceBefore;
    private BigDecimal companyBalanceBefore;
    private BigDecimal companyCommissionBalanceBefore;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private String description;
    private WithdrawRequest withdrawRequest;
    private RefillRequest refillRequest;
}