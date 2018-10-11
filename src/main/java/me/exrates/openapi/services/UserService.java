package me.exrates.openapi.services;


import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.exceptions.AuthenticationNotAvailableException;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.UserRepository;
import org.slf4j.event.Level;
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

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Loggable(caption = "Get authenticated user id")
    @Transactional(readOnly = true)
    public int getAuthenticatedUserId() {
        final String userEmail = getUserEmailFromSecurityContext();

        return userRepository.getIdByEmail(userEmail);
    }

    @Loggable(caption = "Get user role from database by email")
    @Transactional(readOnly = true)
    public UserRole getUserRoleFromDatabaseByEmail(String email) {
        return userRepository.getUserRoleByEmail(email);
    }

    @Loggable(caption = "Get user role from database by id")
    @Transactional(readOnly = true)
    public UserRole getUserRoleFromDatabaseById(Integer userId) {
        return userRepository.getUserRoleById(userId);
    }

    @Loggable(caption = "Get user by id")
    @Transactional(readOnly = true)
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Loggable(caption = "Get user email by id")
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userRepository.getEmailById(id);
    }

    @Loggable(caption = "Get user id by email")
    @Transactional(readOnly = true)
    public int getIdByEmail(String email) {
        return userRepository.getIdByEmail(email);
    }

    @Loggable(caption = "Get user email from security context", logLevel = Level.DEBUG)
    public String getUserEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)) {
            throw new AuthenticationNotAvailableException();
        }
        return authentication.getName();
    }

    @Loggable(caption = "Get user role from security context", logLevel = Level.DEBUG)
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
}