package me.exrates.dao.impl;

import me.exrates.dao.OpenApiTokenDao;
import me.exrates.model.OpenApiToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OpenApiTokenDaoImpl implements OpenApiTokenDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<OpenApiToken> tokenRowMapper = (rs, rowNum) -> {
        OpenApiToken token = new OpenApiToken();
        token.setId(rs.getLong("token_id"));
        token.setUserId(rs.getInt("user_id"));
        token.setUserEmail(rs.getString("email"));
        token.setAlias(rs.getString("alias"));
        token.setPublicKey(rs.getString("public_key"));
        token.setPrivateKey(rs.getString("private_key"));
        token.setAllowTrade(rs.getBoolean("allow_trade"));
        token.setAllowWithdraw(rs.getBoolean("allow_withdraw"));
        token.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
        return token;
    };


}
