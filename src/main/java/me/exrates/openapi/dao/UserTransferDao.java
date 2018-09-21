package me.exrates.openapi.dao;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.model.UserTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class UserTransferDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public UserTransfer save(UserTransfer userTransfer) {
        String sql = "INSERT INTO USER_TRANSFER" +
                " (from_user_id, to_user_id, currency_id, amount, commission_amount)" +
                " VALUES" +
                " (:from_user_id, :to_user_id, :currency_id, :amount, :commission_amount)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("from_user_id", userTransfer.getFromUserId());
            put("to_user_id", userTransfer.getToUserId());
            put("currency_id", userTransfer.getCurrencyId());
            put("amount", userTransfer.getAmount());
            put("commission_amount", userTransfer.getCommissionAmount());
        }};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final int id = (int) keyHolder.getKey().longValue();
        userTransfer.setId(id);
        if (result <= 0) {
            return null;
        }
        return userTransfer;
    }
}
