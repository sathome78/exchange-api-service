package me.exrates.openapi.service;

import me.exrates.openapi.dao.UserRoleDao;
import me.exrates.openapi.model.UserRoleSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleService {

    @Autowired
    UserRoleDao userRoleDao;

    //+
    @Transactional(readOnly = true)
    public boolean isOrderAcceptionAllowedForUser(Integer userId) {
        return userRoleDao.isOrderAcceptionAllowedForUser(userId);
    }

    //+
    @Transactional(readOnly = true)
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return userRoleDao.retrieveSettingsForRole(roleId);
    }
}
