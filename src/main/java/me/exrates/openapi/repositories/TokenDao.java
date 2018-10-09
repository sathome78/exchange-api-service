package me.exrates.openapi.repositories;

import me.exrates.openapi.models.PublicTokenDto;
import me.exrates.openapi.models.Token;
import me.exrates.openapi.repositories.mappers.PublicTokenRowMapper;
import me.exrates.openapi.repositories.mappers.TokenRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TokenDao {

    private static final String SAVE_TOKEN_SQL = "INSERT INTO OPEN_API_USER_TOKEN (user_id, alias, public_key, private_key, allow_trade, allow_withdraw)" +
            " VALUES (:user_id, :alias, :public_key, :private_key, :allow_trade, :allow_withdraw)";

    private static final String GET_BY_PUBLIC_KEY_SQL = "SELECT oaut.id AS token_id, oaut.user_id, u.email, oaut.alias, oaut.public_key, " +
            "oaut.private_key, oaut.date_generation, oaut.allow_trade, oaut.allow_withdraw" +
            " FROM OPEN_API_USER_TOKEN oaut" +
            " JOIN USER u ON oaut.user_id = u.id" +
            " WHERE oaut.public_key = :public_key AND oaut.is_active = 1 ";

    private static final String GET_BY_ID_SQL = "SELECT oaut.id AS token_id, oaut.user_id, U.email, oaut.alias, oaut.public_key, oaut.private_key, " +
            "oaut.date_generation, oaut.allow_trade, oaut.allow_withdraw" +
            " FROM OPEN_API_USER_TOKEN oaut" +
            " JOIN USER u ON oaut.user_id = u.id " +
            " WHERE oaut.id = :id ";

    private static final String GET_ACTIVE_TOKENS_FOR_USER_SQL = "SELECT oaut.id AS token_id, oaut.user_id, oaut.alias, oaut.public_key, oaut.allow_trade, " +
            "oaut.allow_withdraw, oaut.private_key, oaut.date_generation" +
            " FROM OPEN_API_USER_TOKEN oaut" +
            " WHERE oaut.is_active = 1 AND oaut.user_id = (SELECT u.id FROM USER u WHERE u.email = :user_email)";

    private static final String UPDATE_TOKEN_SQL = "UPDATE OPEN_API_USER_TOKEN oaut" +
            " SET oaut.alias = :alias, oaut.allow_trade = :allow_trade, oaut.allow_withdraw = :allow_withdraw" +
            " WHERE oaut.id = :token_id";

    private static final String DEACTIVATE_TOKEN_SQL = "UPDATE OPEN_API_USER_TOKEN oaut" +
            " SET oaut.is_active = 0" +
            " WHERE oaut.id = :token_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TokenDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveToken(Token token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = jdbcTemplate.update(
                SAVE_TOKEN_SQL,
                new MapSqlParameterSource(
                        Map.of(
                                "user_id", token.getUserId(),
                                "alias", token.getAlias(),
                                "public_key", token.getPublicKey(),
                                "private_key", token.getPrivateKey(),
                                "allow_trade", token.getAllowTrade(),
                                "allow_withdraw", token.getAllowWithdraw())),
                keyHolder);

        return update <= 0 ? 0 : Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public Token getByPublicKey(String publicKey) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_BY_PUBLIC_KEY_SQL,
                    Map.of("public_key", publicKey),
                    TokenRowMapper.map());
        } catch (DataAccessException ex) {
            return null;
        }
    }

    public Token getById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_BY_ID_SQL,
                    Map.of("id", id),
                    TokenRowMapper.map());
        } catch (DataAccessException ex) {
            return null;
        }
    }

    public List<PublicTokenDto> getActiveTokensForUser(String userEmail) {
        return jdbcTemplate.query(
                GET_ACTIVE_TOKENS_FOR_USER_SQL,
                Map.of("user_email", userEmail),
                PublicTokenRowMapper.map());
    }

    public void updateToken(Long tokenId, String alias, Boolean allowTrade, Boolean allowWithdraw) {
        jdbcTemplate.update(
                UPDATE_TOKEN_SQL,
                Map.of(
                        "token_id", tokenId,
                        "alias", alias,
                        "allow_trade", allowTrade,
                        "allow_withdraw", allowWithdraw));
    }

    public void deactivateToken(Long tokenId) {
        jdbcTemplate.update(
                DEACTIVATE_TOKEN_SQL,
                Map.of("token_id", tokenId));
    }
}
