package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.CurrencyPairInfoItem;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrencyPairInfoItemRowMapper {

    public static RowMapper<CurrencyPairInfoItem> map() {
        return (rs, row) -> new CurrencyPairInfoItem(rs.getString("name"));
    }
}
