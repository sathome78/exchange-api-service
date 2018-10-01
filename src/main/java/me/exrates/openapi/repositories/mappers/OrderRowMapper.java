package me.exrates.openapi.repositories.mappers;

import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;

public final class OrderRowMapper {

    public static RowMapper<ExOrder> map() {
        return (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setUserAcceptorId(rs.getInt("user_acceptor_id"));
            exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            LocalDateTime dateAcception = LocalDateTime.MIN;
            if (rs.getTimestamp("date_acception") != null) {
                dateAcception = rs.getTimestamp("date_acception").toLocalDateTime();
            }
            exOrder.setDateAcception(dateAcception);
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            return exOrder;
        };
    }
}

