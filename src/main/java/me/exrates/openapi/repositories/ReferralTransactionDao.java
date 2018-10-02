package me.exrates.openapi.repositories;

import me.exrates.openapi.models.ReferralTransaction;
import me.exrates.openapi.models.enums.ReferralTransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ReferralTransactionDao {

    final String sql = "INSERT INTO REFERRAL_TRANSACTION (initiator_id, user_id, order_id, referral_level_id) VALUES (:initiatorId, :userId, :orderId, :refLevelId)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralTransactionDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public void setRefTransactionStatus(ReferralTransactionStatus status, int refTransactionId) {
        String sql = "UPDATE REFERRAL_TRANSACTION " +
                " SET status = :status" +
                " WHERE id = :transaction_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("transaction_id", refTransactionId);
            put("status", status.name());
        }};
        boolean res = jdbcTemplate.update(sql, params) > 0;
        if (!res) throw new RuntimeException("error change status to ref transaction " + refTransactionId);
    }

    //+
    public ReferralTransaction create(final ReferralTransaction referralTransaction) {
        final Map<String, Integer> params = new HashMap<>();
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        params.put("initiatorId", referralTransaction.getInitiatorId());
        params.put("userId", referralTransaction.getUserId());
        params.put("orderId", referralTransaction.getOrder().getId());
        params.put("refLevelId", referralTransaction.getReferralLevel().getId());
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        referralTransaction.setId(keyHolder.getKey().intValue());
        return referralTransaction;
    }
}
