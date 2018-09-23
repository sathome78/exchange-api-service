package me.exrates.openapi.dao;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static java.util.Collections.singletonMap;

@Log4j2
@Repository
public class ReferralUserGraphDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralUserGraphDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer getParent(final Integer child) {
        final String sql = "SELECT parent FROM REFERRAL_USER_GRAPH WHERE child = :child";
        try {
            return jdbcTemplate.queryForObject(sql, singletonMap("child", child), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
