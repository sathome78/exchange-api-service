package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.openAPI.WalletBalanceDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class WalletBalanceRowMapper {

    public static RowMapper<WalletBalanceDto> map() {
        return (rs, rowNum) -> WalletBalanceDto.builder()
                .currencyName(rs.getString("currency_name"))
                .activeBalance(rs.getBigDecimal("active_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .build();
    }
}
