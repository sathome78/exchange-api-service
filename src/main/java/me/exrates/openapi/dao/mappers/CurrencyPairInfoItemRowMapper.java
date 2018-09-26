package me.exrates.openapi.dao.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrencyPairInfoItemRowMapper {

    public static RowMapper<CurrencyPairInfoItem> map() {
        return (rs, row) -> new CurrencyPairInfoItem(rs.getString("name"));
    }
}
