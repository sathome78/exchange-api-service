package me.exrates.openapi.repositories;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.mappers.UserRoleRowMapper;
import me.exrates.openapi.repositories.mappers.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@Repository
public class UserRepository {

    private static final String SELECT_USER_SQL = "SELECT u.id, pu.email AS parent_email, u.finpassword, u.nickname, u.email, " +
            "u.password, u.regdate, u.phone, u.status, ur.name AS role_name" +
            " FROM USER u" +
            " INNER JOIN USER_ROLE ur ON u.roleid = ur.id" +
            " LEFT JOIN REFERRAL_USER_GRAPH rug ON u.id = rug.child" +
            " LEFT JOIN USER pu ON rug.parent = pu.id" +
            " WHERE u.id = :user_id";

    private static final String GET_USER_ROLE_BY_EMAIL_SQL = "SELECT ur.name as role_name" +
            " FROM USER u" +
            " JOIN USER_ROLE ur on u.roleid = ur.id" +
            " WHERE u.email = :email";

    private static final String GET_USER_ROLE_BY_ID_SQL = "SELECT ur.name AS role_name" +
            " FROM USER u" +
            " JOIN USER_ROLE ur ON ur.id = u.roleid" +
            " WHERE u.id = :id";

    private static final String GET_EMAIL_BY_ID_SQL = "SELECT u.email FROM USER u WHERE u.id = :id";

    private static final String GET_ID_BY_EMAIL_SQL = "SELECT u.id FROM USER u WHERE u.email = :email";

    private static final String SELECT_ATTEMPTS_SQL = "SELECT ua.attempts" +
            " FROM USER_API ua" +
            " WHERE ua.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

    private static final String UPDATE_ATTEMPTS_SQL = "UPDATE USER_API ua" +
            " SET ua.attempts = :attempts" +
            " WHERE ua.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

    private static final String INSERT_ATTEMPTS_SQL = "INSERT IGNORE INTO USER_API (user_id, attempts)" +
            " VALUES ((SELECT u.id FROM USER u WHERE u.email = :email), :attempts)";

    private static final String ENABLE_API_FOR_USER_SQL = "UPDATE USER_API ua" +
            " SET ua.enabled = :enabled" +
            " WHERE ua.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

    private static final String ENABLE_API_FOR_ALL_SQL = "UPDATE USER_API ua SET ua.enabled = :enabled";

    private static final String SELECT_USER_ACCESS_POLICY_SQL = "SELECT ua.enabled" +
            " FROM USER_API ua" +
            " WHERE ua.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(int userId) {
        return jdbcTemplate.queryForObject(
                SELECT_USER_SQL,
                Map.of("user_id", userId),
                UserRowMapper.map());
    }

    public UserRole getUserRoleByEmail(String email) {
        return jdbcTemplate.queryForObject(
                GET_USER_ROLE_BY_EMAIL_SQL,
                Map.of("email", email),
                UserRoleRowMapper.map());
    }

    public UserRole getUserRoleById(Integer id) {
        return jdbcTemplate.queryForObject(
                GET_USER_ROLE_BY_ID_SQL,
                Map.of("id", id),
                UserRoleRowMapper.map());
    }

    public String getEmailById(Integer id) {
        return jdbcTemplate.queryForObject(
                GET_EMAIL_BY_ID_SQL,
                Map.of("id", id),
                String.class);
    }

    public int getIdByEmail(String email) {
        try {
            Integer userId = jdbcTemplate.queryForObject(
                    GET_ID_BY_EMAIL_SQL,
                    Map.of("email", email),
                    Integer.class);
            return nonNull(userId) ? userId : 0;
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
    }

    public Integer getRequestsLimit(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_ATTEMPTS_SQL,
                    Map.of("email", email),
                    Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
    }

    public void setRequestsLimit(String email, Integer limit) {
        jdbcTemplate.update(
                INSERT_ATTEMPTS_SQL,
                Map.of(
                        "email", email,
                        "attempts", limit));
    }

    public void updateRequestsLimit(String email, Integer limit) {
        jdbcTemplate.update(
                UPDATE_ATTEMPTS_SQL,
                Map.of(
                        "email", email,
                        "attempts", limit));
    }

    public void enableApiForUser(String userEmail) {
        int update = jdbcTemplate.update(
                ENABLE_API_FOR_USER_SQL,
                Map.of(
                        "enabled", true,
                        "email", userEmail));
        if (update <= 0) {
            log.debug("Api for user: {} have not enabled", userEmail);
        }
    }

    public void disableApiForUser(String email) {
        int update = jdbcTemplate.update(
                ENABLE_API_FOR_USER_SQL,
                Map.of(
                        "enabled", false,
                        "email", email));
        if (update <= 0) {
            log.debug("Api for user: {} have not disabled", email);
        }
    }

    public void enableApiForAll() {
        int update = jdbcTemplate.update(
                ENABLE_API_FOR_ALL_SQL,
                Map.of(
                        "enabled", true));
        if (update <= 0) {
            log.debug("Api for user: {} have not enabled");
        }
    }

    public void disableApiForAll() {
        int update = jdbcTemplate.update(
                ENABLE_API_FOR_ALL_SQL,
                Map.of(
                        "enabled", false));
        if (update <= 0) {
            log.debug("Api for user: {} have not disabled");
        }
    }

    public Boolean isEnabled(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_USER_ACCESS_POLICY_SQL,
                    Map.of("email", email),
                    Boolean.class);
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }
}
