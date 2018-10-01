package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrencyPairLimitRowMapper {

    public static RowMapper<CurrencyPairLimitDto> map() {
        return (rs, rowNum) -> CurrencyPairLimitDto.builder()
                .currencyPairId(rs.getInt("currency_pair_id"))
                .currencyPairName(rs.getString("currency_pair_name"))
                .minRate(rs.getBigDecimal("min_rate"))
                .maxRate(rs.getBigDecimal("max_rate"))
                .minAmount(rs.getBigDecimal("min_amount"))
                .maxAmount(rs.getBigDecimal("max_amount"))
                .build();
    }
}
