package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.TradeHistoryDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class TradeHistoryRowMapper {

    public static RowMapper<TradeHistoryDto> map() {
        return (rs, row) -> {
            TradeHistoryDto tradeHistoryDto = new TradeHistoryDto();
            tradeHistoryDto.setOrderId(rs.getInt("order_id"));
            tradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            tradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            tradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            tradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            tradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            tradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            tradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return tradeHistoryDto;
        };
    }
}
