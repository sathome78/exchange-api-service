package me.exrates.openapi.services;

import me.exrates.openapi.models.UserRoleSettings;
import me.exrates.openapi.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public boolean isOrderAcceptanceAllowedForUser(Integer userId) {
        return userRoleRepository.isOrderAcceptanceAllowedForUser(userId);
    }

    @Transactional(readOnly = true)
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return userRoleRepository.retrieveSettingsForRole(roleId);
    }
}
