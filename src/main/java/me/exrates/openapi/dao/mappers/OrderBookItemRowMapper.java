package me.exrates.openapi.dao.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.model.dto.openAPI.OrderBookItem;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.OrderType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class OrderBookItemRowMapper {

    public static RowMapper<OrderBookItem> map() {
        return (rs, row) -> OrderBookItem.builder()
                .orderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))))
                .amount(rs.getBigDecimal("amount"))
                .rate(rs.getBigDecimal("price"))
                .build();
    }
}
