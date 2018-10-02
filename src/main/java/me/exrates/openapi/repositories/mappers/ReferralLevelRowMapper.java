package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.ReferralLevel;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ReferralLevelRowMapper {

    public static RowMapper<ReferralLevel> map() {
        return (rs, i) -> ReferralLevel.builder()
                .id(rs.getInt("id"))
                .percent(rs.getBigDecimal("percent"))
                .level(rs.getInt("level"))
                .build();
    }
}
