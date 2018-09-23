package me.exrates.openapi.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class NotificatorPriceDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BigDecimal getFeeMessagePrice(int notificatorId, int roleId) {
        final String sql = "SELECT message_price FROM 2FA_NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        final String sql = "SELECT subscribe_price FROM 2FA_NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }
}
