package me.exrates.openapi.dao;

import me.exrates.openapi.model.dto.TelegramSubscription;
import me.exrates.openapi.model.enums.NotificatorSubscriptionStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TelegramSubscriptionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<TelegramSubscription> telegramSubscribtionRowMapper = (rs, idx) -> {
        TelegramSubscription subscription = new TelegramSubscription();
        subscription.setId(rs.getInt("id"));
        subscription.setChatId(rs.getLong("chat_id"));
        subscription.setUserId(rs.getInt("user_id"));
        subscription.setUserAccount(rs.getString("user_account"));
        subscription.setCode(rs.getString("code"));
        subscription.setSubscriptionState(NotificatorSubscriptionStateEnum.valueOf(rs.getString("subscription_state")));
        return subscription;
    };

    public Optional<TelegramSubscription> getSubscribtionByCodeAndEmail(String code, String email) {
        final String sql = " SELECT * FROM TELEGRAM_SUBSCRIPTION " +
                " INNER JOIN USER U ON U.email = :email " +
                " WHERE code = :code ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code)
                .addValue("email", email);
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, telegramSubscribtionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateSubscription(TelegramSubscription subscribtion) {
        final String sql = " UPDATE TELEGRAM_SUBSCRIPTION " +
                " SET code = :code, subscription_state = :subscription_state, user_account = :user_account, chat_id = :chat_id " +
                " WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", subscribtion.getId());
        params.put("code", subscribtion.getCode());
        params.put("subscription_state", subscribtion.getSubscriptionState().name());
        params.put("user_account", subscribtion.getUserAccount());
        params.put("chat_id", subscribtion.getChatId());
        jdbcTemplate.update(sql, params);
    }
}
