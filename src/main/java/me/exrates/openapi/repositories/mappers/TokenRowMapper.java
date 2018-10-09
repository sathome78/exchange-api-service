package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.Token;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class TokenRowMapper {

    public static RowMapper<Token> map() {
        return (rs, rowNum) -> Token.builder()
                .id(rs.getLong("token_id"))
                .userId(rs.getInt("user_id"))
                .userEmail(rs.getString("email"))
                .alias(rs.getString("alias"))
                .publicKey(rs.getString("public_key"))
                .privateKey(rs.getString("private_key"))
                .allowTrade(rs.getBoolean("allow_trade"))
                .allowWithdraw(rs.getBoolean("allow_withdraw"))
                .generationDate(rs.getTimestamp("date_generation").toLocalDateTime())
                .build();
    }
}
