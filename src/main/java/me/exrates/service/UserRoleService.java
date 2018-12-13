package me.exrates.service;

import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.GroupUserRoleEnum;
import me.exrates.model.enums.UserRole;

import java.util.List;

public interface UserRoleService {

    List<UserRole> getRealUserRoleByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

    List<Integer> getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

    String[] getRealUserRoleNameByBusinessRoleArray(BusinessUserRoleEnum businessUserRoleEnum);

    List<Integer> getRealUserRoleIdByBusinessRoleList(String businessUserRoleName);

    List<UserRole> getRealUserRoleByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

    List<Integer> getRealUserRoleIdByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

    List<Integer> getRealUserRoleIdByGroupRoleList(String groupUserRoleName);

    boolean isOrderAcceptionAllowedForUser(Integer userId);

    UserRoleSettings retrieveSettingsForRole(Integer roleId);

    List<UserRole> getRolesAvailableForChangeByAdmin();

    List<UserRoleSettings> retrieveSettingsForAllRoles();

    void updateSettingsForRole(UserRoleSettings settings);

    List<UserRole> getRolesConsideredForPriceRangeComputation();

    List<UserRole> getRolesUsingRealMoney();
}
