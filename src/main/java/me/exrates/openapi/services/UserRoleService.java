package me.exrates.openapi.services;

import me.exrates.openapi.models.UserRoleSettings;
import me.exrates.openapi.repositories.UserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleService {

    private final UserRoleDao userRoleDao;

    @Autowired
    public UserRoleService(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    //+
    @Transactional(readOnly = true)
    public boolean isOrderAcceptanceAllowedForUser(Integer userId) {
        return userRoleDao.isOrderAcceptanceAllowedForUser(userId);
    }

    //+
    @Transactional(readOnly = true)
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return userRoleDao.retrieveSettingsForRole(roleId);
    }
}
