package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.OpenOrderDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class OpenOrderRowMapper {

    public static RowMapper<OpenOrderDto> map() {
        return (rs, row) -> OpenOrderDto.builder()
                .id(rs.getInt("id"))
                .orderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name())
                .amount(rs.getBigDecimal("amount_base"))
                .price(rs.getBigDecimal("exrate"))
                .build();
    }
}
