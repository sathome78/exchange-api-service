package me.exrates.openapi.services;


import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.AuthenticationNotAvailableException;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static me.exrates.openapi.models.enums.UserRole.USER;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    //+
    public int getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    //+
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    //+
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    //+
    @Transactional
    public UserRole getUserRoleFromDatabase(Integer userId) {
        return userDao.getUserRoleById(userId);
    }

    //+
    @Transactional(readOnly = true)
    public int getAuthenticatedUserId() {
        final String userEmail = getUserEmailFromSecurityContext();

        return userDao.getIdByEmail(userEmail);
    }

    //+
    public UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = Stream.of(UserRole.values())
                .map(UserRole::name)
                .collect(toSet());

        String grantedAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(roles::contains)
                .findFirst()
                .orElse(USER.name());

        log.debug("Granted authority: {}", grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }

    //+
    @Transactional(readOnly = true)
    public UserRole getUserRoleFromDatabase(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    //+
    public String getUserEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)) {
            throw new AuthenticationNotAvailableException();
        }
        return authentication.getName();
    }
}