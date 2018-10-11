package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.CurrencyPairInfo;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrencyPairInfoItemRowMapper {

    public static RowMapper<CurrencyPairInfo> map() {
        return (rs, row) -> new CurrencyPairInfo(rs.getString("name"));
    }
}
