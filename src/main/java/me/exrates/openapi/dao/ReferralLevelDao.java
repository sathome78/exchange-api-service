package me.exrates.openapi.dao;

import me.exrates.openapi.model.ReferralLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Collections.emptyList;

@Repository
public class ReferralLevelDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    protected static RowMapper<ReferralLevel> referralLevelRowMapper = (resultSet, i) -> {
        final ReferralLevel result = new ReferralLevel();
        result.setPercent(resultSet.getBigDecimal("REFERRAL_LEVEL.percent"));
        result.setLevel(resultSet.getInt("REFERRAL_LEVEL.level"));
        result.setId(resultSet.getInt("REFERRAL_LEVEL.id"));
        return result;
    };

    @Autowired
    public ReferralLevelDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReferralLevel> findAll() {
        final String sql = "SELECT REFERRAL_LEVEL.*  FROM REFERRAL_LEVEL REFERRAL_LEVEL LEFT JOIN REFERRAL_LEVEL b ON REFERRAL_LEVEL.level = b.level AND REFERRAL_LEVEL.datetime < b.datetime WHERE b.datetime is NULL ORDER BY level;";
        try {
            return jdbcTemplate.query(sql, referralLevelRowMapper);
        } catch (final EmptyResultDataAccessException ignore) {
            return emptyList();
        }
    }
}
