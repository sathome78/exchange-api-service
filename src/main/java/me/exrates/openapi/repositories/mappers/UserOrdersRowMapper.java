package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import org.springframework.jdbc.core.RowMapper;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserOrdersRowMapper {

    public static RowMapper<UserOrdersDto> map() {
        return (rs, row) -> UserOrdersDto.builder()
                .id(rs.getInt("order_id"))
                .currencyPair(rs.getString("currency_pair_name"))
                .orderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name())
                .amount(rs.getBigDecimal("amount"))
                .price(rs.getBigDecimal("price"))
                .dateCreation(rs.getTimestamp("created").toLocalDateTime())
                .dateAcceptance(nonNull(rs.getTimestamp("accepted")) ? rs.getTimestamp("accepted").toLocalDateTime() : null)
                .build();
    }
}
