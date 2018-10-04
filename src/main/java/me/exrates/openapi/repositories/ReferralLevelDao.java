package me.exrates.openapi.repositories;

import me.exrates.openapi.models.ReferralLevel;
import me.exrates.openapi.repositories.mappers.ReferralLevelRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Collections.emptyList;

@Repository
public class ReferralLevelDao {

    private static final String FIND_ALL_REFERRAL_LEVELS_SQL = "SELECT *" +
            " FROM REFERRAL_LEVEL rl1" +
            " LEFT JOIN REFERRAL_LEVEL rl2 ON rl2.level = rl1.level AND rl1.datetime < rl2.datetime" +
            " WHERE rl2.datetime is NULL" +
            " ORDER BY rl1.level";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralLevelDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReferralLevel> findAll() {
        try {
            return jdbcTemplate.query(
                    FIND_ALL_REFERRAL_LEVELS_SQL,
                    ReferralLevelRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }
}
