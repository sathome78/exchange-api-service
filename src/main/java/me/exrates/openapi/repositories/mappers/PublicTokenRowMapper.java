package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.PublicTokenDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class PublicTokenRowMapper {

    public static RowMapper<PublicTokenDto> map() {
        return (rs, rowNum) -> PublicTokenDto.builder()
                .id(rs.getLong("token_id"))
                .userId(rs.getInt("user_id"))
                .alias(rs.getString("alias"))
                .publicKey(rs.getString("public_key"))
                .allowTrade(rs.getBoolean("allow_trade"))
                .allowWithdraw(rs.getBoolean("allow_withdraw"))
                .generationDate(rs.getTimestamp("date_generation").toLocalDateTime())
                .build();
    }
}
