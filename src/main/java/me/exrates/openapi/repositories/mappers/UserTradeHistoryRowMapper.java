package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.UserTradeHistoryDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserTradeHistoryRowMapper {

    public static RowMapper<UserTradeHistoryDto> map(int userId) {
        return (rs, row) -> {
            UserTradeHistoryDto userTradeHistoryDto = new UserTradeHistoryDto();
            userTradeHistoryDto.setUserId(userId);
            userTradeHistoryDto.setIsMaker(userId == rs.getInt("user_id"));
            userTradeHistoryDto.setOrderId(rs.getInt("order_id"));
            userTradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            userTradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            userTradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            userTradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            userTradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            userTradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            userTradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return userTradeHistoryDto;
        };
    }
}
