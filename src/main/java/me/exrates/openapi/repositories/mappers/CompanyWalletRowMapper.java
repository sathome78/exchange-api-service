package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CompanyWalletRowMapper {

    public static RowMapper<CompanyWallet> map() {
        return (rs, rowNum) -> CompanyWallet.builder()
                .id(rs.getInt("company_wallet_id"))
                .currency(Currency.builder()
                        .id(rs.getInt("currency_id"))
                        .build())
                .balance(rs.getBigDecimal("balance"))
                .commissionBalance(rs.getBigDecimal("commission_balance"))
                .build();
    }
}
