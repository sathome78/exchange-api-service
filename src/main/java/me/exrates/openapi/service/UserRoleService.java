package me.exrates.openapi.service;

import me.exrates.openapi.dao.UserRoleDao;
import me.exrates.openapi.model.UserRoleSettings;
import me.exrates.openapi.model.enums.BusinessUserRoleEnum;
import me.exrates.openapi.model.enums.GroupUserRoleEnum;
import me.exrates.openapi.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    @Autowired
    UserRoleDao userRoleDao;

    @Transactional(readOnly = true)
    public List<UserRole> getRealUserRoleByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum) {
        return userRoleDao.findRealUserRoleIdByBusinessRoleList(businessUserRoleEnum.name());
    }

    @Transactional(readOnly = true)
    public List<Integer> getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum) {
        return getRealUserRoleByBusinessRoleList(businessUserRoleEnum).stream()
                .map(e -> e.getRole())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String[] getRealUserRoleNameByBusinessRoleArray(BusinessUserRoleEnum businessUserRoleEnum) {
        return getRealUserRoleByBusinessRoleList(businessUserRoleEnum).stream()
                .toArray(size -> new String[size]);
    }

    @Transactional(readOnly = true)
    public List<Integer> getRealUserRoleIdByBusinessRoleList(String businessUserRoleName) {
        if ("ALL".equals(businessUserRoleName)) {
            return Collections.EMPTY_LIST;
        } else {
            BusinessUserRoleEnum businessUserRoleEnum = BusinessUserRoleEnum.convert(businessUserRoleName);
            return getRealUserRoleIdByBusinessRoleList(businessUserRoleEnum);
        }
    }

    @Transactional(readOnly = true)
    public List<UserRole> getRealUserRoleByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum) {
        return userRoleDao.findRealUserRoleIdByGroupRoleList(groupUserRoleEnum.name());
    }

    @Transactional(readOnly = true)
    public List<Integer> getRealUserRoleIdByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum) {
        return getRealUserRoleByGroupRoleList(groupUserRoleEnum).stream()
                .map(e -> e.getRole())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Integer> getRealUserRoleIdByGroupRoleList(String groupUserRoleName) {
        GroupUserRoleEnum businessUserRoleEnum = GroupUserRoleEnum.convert(groupUserRoleName);
        return getRealUserRoleIdByGroupRoleList(businessUserRoleEnum);
    }

    @Transactional(readOnly = true)
    public boolean isOrderAcceptionAllowedForUser(Integer userId) {
        return userRoleDao.isOrderAcceptionAllowedForUser(userId);
    }

    @Transactional(readOnly = true)
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return userRoleDao.retrieveSettingsForRole(roleId);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getRolesAvailableForChangeByAdmin() {
        return userRoleDao.getRolesAvailableForChangeByAdmin();
    }

    @Transactional(readOnly = true)
    public List<UserRoleSettings> retrieveSettingsForAllRoles() {
        return userRoleDao.retrieveSettingsForAllRoles();
    }

    @Transactional
    public void updateSettingsForRole(UserRoleSettings settings) {
        userRoleDao.updateSettingsForRole(settings);
    }
}
