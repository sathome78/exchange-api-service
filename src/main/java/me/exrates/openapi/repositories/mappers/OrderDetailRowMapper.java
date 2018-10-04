package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.OrderDetailDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.NONE)
public class OrderDetailRowMapper {

    public static RowMapper<OrderDetailDto> map() {
        return (rs, rowNum) -> {
            BigDecimal orderCreatorReservedAmount = rs.getInt("operation_type_id") == 3
                    ? rs.getBigDecimal("amount_base")
                    : BigDecimalProcessingUtil.doAction(
                    rs.getBigDecimal("amount_convert"),
                    rs.getBigDecimal("commission_fixed_amount"),
                    ActionType.ADD);
            return OrderDetailDto.builder()
                    .orderId(rs.getInt("order_id"))
                    .orderStatus(OrderStatus.convert(rs.getInt("order_status_id")))
                    .orderCreatorReservedAmount(orderCreatorReservedAmount)
                    .orderCreatorReservedWalletId(rs.getInt("order_creator_reserved_wallet_id"))
                    .transactionId(rs.getInt("transaction_id"))
                    .transactionType(OperationType.convert(rs.getInt("transaction_type_id")))
                    .transactionAmount(rs.getBigDecimal("transaction_amount"))
                    .userWalletId(rs.getInt("user_wallet_id"))
                    .companyWalletId(rs.getInt("company_wallet_id"))
                    .companyCommission(rs.getBigDecimal("company_commission"))
                    .build();
        };
    }
}
