package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionStatus;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class TransactionRowMapper {

    public static RowMapper<TransactionDto> map() {
        return (rs, row) -> TransactionDto.builder()
                .transactionId(rs.getInt("id"))
                .walletId(rs.getInt("user_wallet_id"))
                .amount(rs.getBigDecimal("amount"))
                .commission(rs.getBigDecimal("commission"))
                .currency(rs.getString("currency"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .status(TransactionStatus.convert(rs.getInt("status_id")))
                .build();
    }
}
