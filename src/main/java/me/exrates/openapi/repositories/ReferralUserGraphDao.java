package me.exrates.openapi.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ReferralUserGraphDao {

    private static final String GET_PARENT_SQL = "SELECT rug.parent FROM REFERRAL_USER_GRAPH rug WHERE rug.child = :child";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralUserGraphDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer getParent(Integer child) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_PARENT_SQL,
                    Map.of("child", child),
                    Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
