package me.exrates.openapi.repositories;

import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.mappers.UserRoleRowMapper;
import me.exrates.openapi.repositories.mappers.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDao {

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

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public int getIdByEmail(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    //+
    public String getPreferredLang(int userId) {
        String sql = "SELECT preferred_lang FROM USER WHERE id = :id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", userId);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public String getEmailById(Integer id) {
        String sql = "SELECT email FROM USER WHERE id = :id";
        return jdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class);
    }

    //+
    public User getUserById(int userId) {
        return jdbcTemplate.queryForObject(
                SELECT_USER_SQL,
                Map.of("user_id", userId),
                UserRowMapper.map());
    }

    //+
    public UserRole getUserRoleByEmail(String email) {
        return jdbcTemplate.queryForObject(
                GET_USER_ROLE_BY_EMAIL_SQL,
                Map.of("email", email),
                UserRoleRowMapper.map());
    }

    //+
    public UserRole getUserRoleById(Integer id) {
        return jdbcTemplate.queryForObject(
                GET_USER_ROLE_BY_ID_SQL,
                Map.of("id", id),
                UserRoleRowMapper.map());
    }
}
