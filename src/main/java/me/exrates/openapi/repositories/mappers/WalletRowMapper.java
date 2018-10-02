package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.Wallet;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class WalletRowMapper {

    public static RowMapper<Wallet> map() {
        return (rs, rowNum) -> Wallet.builder()
                .id(rs.getInt("wallet_id"))
                .currencyId(rs.getInt("currency_id"))
                .activeBalance(rs.getBigDecimal("active_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .build();
    }
}
