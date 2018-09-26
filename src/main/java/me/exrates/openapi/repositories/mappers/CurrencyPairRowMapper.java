package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.enums.CurrencyPairType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrencyPairRowMapper {

    public static RowMapper<CurrencyPair> map() {
        return (rs, row) -> CurrencyPair.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .pairType(CurrencyPairType.valueOf(rs.getString("type")))
                .currency1(Currency.builder()
                        .id(rs.getInt("currency1_id"))
                        .name(rs.getString("currency1_name"))
                        .build())
                .currency2(Currency.builder()
                        .id(rs.getInt("currency2_id"))
                        .name(rs.getString("currency2_name"))
                        .build())
                .market(rs.getString("market"))
                .build();
    }
}
