package me.exrates.openapi.repositories;

import me.exrates.openapi.models.UserRoleSettings;
import me.exrates.openapi.repositories.mappers.UserRoleSettingsRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRoleRepository {

    private static final String IS_ORDER_ACCEPTANCE_ALLOWED_FOR_USER_SQL = "SELECT urs.order_acception_same_role_only" +
            " FROM USER_ROLE_SETTINGS urs" +
            " WHERE urs.user_role_id = (SELECT roleid FROM USER u WHERE u.id = :user_id)";

    private static final String RETRIEVE_SETTINGS_FOR_ROLE_SQL = "SELECT urs.user_role_id, urs.order_acception_same_role_only, " +
            "urs.bot_acception_allowed, urs.manual_change_allowed" +
            " FROM USER_ROLE_SETTINGS urs" +
            " WHERE urs.user_role_id = :user_role_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRoleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean isOrderAcceptanceAllowedForUser(Integer userId) {
        return jdbcTemplate.queryForObject(
                IS_ORDER_ACCEPTANCE_ALLOWED_FOR_USER_SQL,
                Map.of("user_id", userId),
                Boolean.class);
    }

    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return jdbcTemplate.queryForObject(
                RETRIEVE_SETTINGS_FOR_ROLE_SQL,
                Map.of("user_role_id", roleId),
                UserRoleSettingsRowMapper.map());
    }
}
