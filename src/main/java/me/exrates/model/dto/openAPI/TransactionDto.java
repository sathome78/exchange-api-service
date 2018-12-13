package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class TransactionDto {

    @JsonProperty("transaction_id")
    private Integer transactionId;

    @JsonProperty("wallet_id")
    private Integer walletId;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal commission;

    private String currency;

    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime time;

    @JsonProperty("operation_type")
    private OperationType operationType;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    @JsonProperty("transaction_status")
    private TransactionStatus transactionStatus;
}
