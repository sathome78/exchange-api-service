package me.exrates.openapi.services;


import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.AbsentFinPasswordException;
import me.exrates.openapi.exceptions.AuthenticationNotAvailableException;
import me.exrates.openapi.exceptions.NotConfirmedFinPasswordException;
import me.exrates.openapi.exceptions.WrongFinPasswordException;
import me.exrates.openapi.models.TemporalToken;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.TokenType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static me.exrates.openapi.models.enums.UserRole.USER;

@Slf4j
@Service
public class UserService {

    private final UserRole ROLE_DEFAULT_COMMISSION = USER;

    private final UserDao userDao;
    private final MessageSource messageSource;

    @Autowired
    public UserService(UserDao userDao,
                       MessageSource messageSource) {
        this.userDao = userDao;
        this.messageSource = messageSource;
    }

    /*
     * for checking if there are open tokens of concrete type for the user
     * */
    public List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType) {
        return userDao.getTokenByUserAndType(user.getId(), tokenType);
    }

    //+
    public int getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    //+
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void checkFinPassword(String enteredFinPassword, User storedUser, Locale locale) {
        boolean isNotConfirmedToken = getTokenByUserAndType(storedUser, TokenType.CHANGE_FIN_PASSWORD).size() > 0;
        if (isNotConfirmedToken) {
            throw new NotConfirmedFinPasswordException(messageSource.getMessage("admin.notconfirmedfinpassword", null, locale));
        }
        String currentFinPassword = storedUser.getFinpassword();
        if (currentFinPassword == null || currentFinPassword.isEmpty()) {
            throw new AbsentFinPasswordException(messageSource.getMessage("admin.absentfinpassword", null, locale));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authSuccess = passwordEncoder.matches(enteredFinPassword, currentFinPassword);
        if (!authSuccess) {
            throw new WrongFinPasswordException(messageSource.getMessage("admin.wrongfinpassword", null, locale));
        }
    }

    //+
    public String getPreferedLang(int userId) {
        return userDao.getPreferredLang(userId);
    }

    //+
    public String getPreferedLangByEmail(String email) {
        return userDao.getPreferredLangByEmail(email);
    }

    //+
    public Locale getUserLocaleForMobile(String email) {
        String lang = getPreferedLangByEmail(email);
        //adaptation for locales available in mobile app
        if (!("ru".equalsIgnoreCase(lang) || "en".equalsIgnoreCase(lang))) {
            lang = "en";
        }
        return new Locale(lang);
    }

    //+
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    //+
    public UserRole getUserRoleFromDB(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    //+
    @Transactional
    public UserRole getUserRoleFromDB(Integer userId) {
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
    public String getUserEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)) {
            throw new AuthenticationNotAvailableException();
        }
        return authentication.getName();
    }
}