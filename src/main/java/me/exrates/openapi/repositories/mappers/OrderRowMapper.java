package me.exrates.openapi.repositories.mappers;

import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

public final class OrderRowMapper {

    public static RowMapper<ExOrder> map() {
        return (rs, row) -> ExOrder.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .currencyPairId(rs.getInt("currency_pair_id"))
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .exRate(rs.getBigDecimal("exrate"))
                .amountBase(rs.getBigDecimal("amount_base"))
                .comissionId(rs.getInt("commission_id"))
                .amountConvert(rs.getBigDecimal("amount_convert"))
                .commissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"))
                .userAcceptorId(rs.getInt("user_acceptor_id"))
                .dateCreation(rs.getTimestamp("date_creation").toLocalDateTime())
                .dateAcception(nonNull(rs.getTimestamp("date_acception")) ? rs.getTimestamp("date_acception").toLocalDateTime() : LocalDateTime.MIN)
                .status(OrderStatus.convert(rs.getInt("status_id")))
                .orderBaseType(OrderBaseType.valueOf(rs.getString("base_type")))
                .build();
    }
}

