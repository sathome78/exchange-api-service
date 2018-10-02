package me.exrates.openapi.models.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WalletOperationData {

    private OperationType operationType;
    private int walletId;
    private BigDecimal amount;
    private BalanceType balanceType;
    private Commission commission;
    private BigDecimal commissionAmount;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private Transaction transaction;
    private String description;

    public enum BalanceType {
        ACTIVE,
        RESERVED
    }
}
