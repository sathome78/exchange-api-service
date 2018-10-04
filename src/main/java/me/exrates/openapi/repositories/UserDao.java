package me.exrates.openapi.repositories;

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

    private static final String GET_EMAIL_BY_ID_SQL = "SELECT u.email FROM USER u WHERE u.id = :id";

    private static final String GET_ID_BY_EMAIL_SQL = "SELECT u.id FROM USER u WHERE u.email = :email";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(NamedParameterJdbcTemplate jdbcTemplate) {
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
}
