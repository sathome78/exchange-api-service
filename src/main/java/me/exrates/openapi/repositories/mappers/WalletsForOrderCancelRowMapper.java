package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;

import static me.exrates.openapi.models.enums.OperationType.SELL;

@NoArgsConstructor(access = AccessLevel.NONE)
public class WalletsForOrderCancelRowMapper {

    public static RowMapper<WalletsForOrderCancelDto> map(OperationType operationType) {
        return (rs, i) -> {
            BigDecimal reservedAmount = operationType == SELL
                    ? rs.getBigDecimal("amount_base")
                    : BigDecimalProcessingUtil.doAction(rs.getBigDecimal("amount_convert"), rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);

            return WalletsForOrderCancelDto.builder()
                    .orderId(rs.getInt("order_id"))
                    .orderStatusId(rs.getInt("order_status_id"))
                    .reservedAmount(reservedAmount)
                    .walletId(rs.getInt("wallet_id"))
                    .activeBalance(rs.getBigDecimal("active_balance"))
                    .reservedBalance(rs.getBigDecimal("reserved_balance"))
                    .build();
        };
    }
}
