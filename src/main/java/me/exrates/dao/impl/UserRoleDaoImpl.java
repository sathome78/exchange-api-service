package me.exrates.dao.impl;

import me.exrates.dao.UserRoleDao;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

/**
 * Created by ValkSam on 09.03.2017.
 */
@Repository
public class UserRoleDaoImpl implements UserRoleDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public List<UserRole> findRealUserRoleIdByBusinessRoleList(String businessRoleName) {
    String sql = "SELECT UR.id " +
        "  FROM USER_ROLE UR " +
        "  JOIN USER_ROLE_BUSINESS_FEATURE URBF ON (URBF.id = UR.user_role_business_feature_id) AND (URBF.name = :business_role_name) ";
    return namedParameterJdbcTemplate.query(sql, singletonMap("business_role_name", businessRoleName), (resultSet, i) ->
            UserRole.convert(resultSet.getInt("id")));
  }

  @Override
  public List<UserRole> findRealUserRoleIdByGroupRoleList(String groupRoleName) {
    String sql = "SELECT UR.id " +
        "  FROM USER_ROLE UR " +
        "  JOIN USER_ROLE_GROUP_FEATURE URGF ON (URGF.id = UR.user_role_group_feature_id) AND (URGF.name = :group_role_name) ";
    return namedParameterJdbcTemplate.query(sql, singletonMap("group_role_name", groupRoleName), (resultSet, i) ->
            UserRole.convert(resultSet.getInt("id")));
  }

  @Override
  public boolean isOrderAcceptionAllowedForUser(Integer userId) {
    String sql = "SELECT order_acception_same_role_only FROM USER_ROLE_SETTINGS where user_role_id = (SELECT roleid FROM USER WHERE id = :user_id)";
    return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_id", userId), Boolean.class);
  }

  @Override
  public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
    String sql = "SELECT user_role_id, order_acception_same_role_only, bot_acception_allowed, manual_change_allowed " +
            " FROM USER_ROLE_SETTINGS where user_role_id = :user_role_id";
    return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_role_id", roleId), (rs, rowNum) -> {
      UserRoleSettings settings = new UserRoleSettings();
      settings.setUserRole(UserRole.convert(rs.getInt("user_role_id")));
      settings.setOrderAcceptionSameRoleOnly(rs.getBoolean("order_acception_same_role_only"));
      settings.setBotAcceptionAllowedOnly(rs.getBoolean("bot_acception_allowed"));
      settings.setManualChangeAllowed(rs.getBoolean("manual_change_allowed"));
      return settings;
    });
  }

  @Override
  public List<UserRoleSettings> retrieveSettingsForAllRoles() {
    String sql = "SELECT user_role_id, order_acception_same_role_only, bot_acception_allowed, considered_for_price_range FROM USER_ROLE_SETTINGS";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      UserRoleSettings settings = new UserRoleSettings();
      settings.setUserRole(UserRole.convert(rs.getInt("user_role_id")));
      settings.setOrderAcceptionSameRoleOnly(rs.getBoolean("order_acception_same_role_only"));
      settings.setBotAcceptionAllowedOnly(rs.getBoolean("bot_acception_allowed"));
      settings.setConsideredForPriceRange(rs.getBoolean("considered_for_price_range"));
      return settings;
    });
  }

  @Override
  public void updateSettingsForRole(UserRoleSettings settings) {
    String sql = "UPDATE USER_ROLE_SETTINGS SET order_acception_same_role_only = :same_role_only, bot_acception_allowed = :bot_acception_allowed, " +
            "considered_for_price_range = :considered_for_price_range " +
            "WHERE user_role_id = :role_id";
    Map<String, Object> params = new HashMap<>();
    params.put("role_id", settings.getUserRole().getRole());
    params.put("same_role_only", settings.isOrderAcceptionSameRoleOnly());
    params.put("bot_acception_allowed", settings.isBotAcceptionAllowedOnly());
    params.put("considered_for_price_range", settings.isConsideredForPriceRange());
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public List<UserRole> getRolesAvailableForChangeByAdmin() {
    String sql = "SELECT user_role_id FROM USER_ROLE_SETTINGS WHERE manual_change_allowed = 1";
    return jdbcTemplate.queryForList(sql, Integer.class).stream().map(UserRole::convert).collect(Collectors.toList());
  }

  @Override
  public List<UserRole> getRolesConsideredForPriceRangeComputation() {
    String sql = "SELECT user_role_id FROM USER_ROLE_SETTINGS WHERE considered_for_price_range = 1";
    return jdbcTemplate.queryForList(sql, Integer.class).stream().map(UserRole::convert).collect(Collectors.toList());
  }

  @Override
  public List<UserRole> getRolesUsingRealMoney() {
    String sql = "SELECT user_role_id FROM USER_ROLE_SETTINGS WHERE use_real_money = 1";
    return jdbcTemplate.queryForList(sql, Integer.class).stream().map(UserRole::convert).collect(Collectors.toList());
  }

}
