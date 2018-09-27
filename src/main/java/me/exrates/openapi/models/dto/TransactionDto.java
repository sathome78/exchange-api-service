package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    @JsonProperty("transaction_id")
    private Integer transactionId;
    @JsonProperty("wallet_id")
    private Integer walletId;
    private BigDecimal amount;
    private BigDecimal commission;
    private String currency;
    private LocalDateTime time;
    @JsonProperty("operation_type")
    private OperationType operationType;
    @JsonProperty("transaction_status")
    private TransactionStatus status;
}
