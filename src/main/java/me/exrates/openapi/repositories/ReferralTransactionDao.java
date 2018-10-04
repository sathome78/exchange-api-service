package me.exrates.openapi.repositories;

import me.exrates.openapi.models.ReferralTransaction;
import me.exrates.openapi.models.enums.ReferralTransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

@Repository
public class ReferralTransactionDao {

    private static final String CREATE_REFERRAL_TRANSACTION_SQL = "INSERT INTO REFERRAL_TRANSACTION (initiator_id, user_id, order_id, referral_level_id)" +
            " VALUES (:initiatorId, :userId, :orderId, :refLevelId)";

    private static final String SET_REFERRAL_TRANSACTION_STATUS_SQL = "UPDATE REFERRAL_TRANSACTION rt" +
            " SET rt.status = :status" +
            " WHERE rt.id = :transaction_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralTransactionDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ReferralTransaction create(final ReferralTransaction referralTransaction) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                CREATE_REFERRAL_TRANSACTION_SQL,
                new MapSqlParameterSource(
                        Map.of(
                                "initiatorId", referralTransaction.getInitiatorId(),
                                "userId", referralTransaction.getUserId(),
                                "orderId", referralTransaction.getOrder().getId(),
                                "refLevelId", referralTransaction.getReferralLevel().getId())),
                keyHolder);

        referralTransaction.setId(Objects.requireNonNull(keyHolder.getKey(), "Key should be present").intValue());
        return referralTransaction;
    }

    public void setRefTransactionStatus(int refTransactionId) {
        int update = jdbcTemplate.update(
                SET_REFERRAL_TRANSACTION_STATUS_SQL,
                Map.of(
                        "transaction_id", refTransactionId,
                        "status", ReferralTransactionStatus.DELETED.name()));
        if (update <= 0) {
            throw new RuntimeException("Error change status to referral transaction: " + refTransactionId);
        }
    }
}
