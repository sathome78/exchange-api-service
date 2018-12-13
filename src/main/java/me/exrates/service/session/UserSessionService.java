package me.exrates.service.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
/**
 * Class for work with the sessions by users
 */
public class UserSessionService {

    @Autowired
    @Qualifier("ExratesSessionRegistry")
    private SessionRegistry sessionRegistry;

    /**
     * Method expire user session(-s) except specific session with specific session id (2nd parameter)
     * 1st parameter (String userEmail):
     *  - user email (get user by email);
     * 2nd parameter (String specificSessionId):
     *  - RequestContextHolder.currentRequestAttributes().getSessionId() - get current session id;
     *  - null (when null, expire all session of user);
     *  - other String (session id);
     * @param userEmail
     * @param specificSessionId
     */
    public void invalidateUserSessionExceptSpecific(String userEmail, String specificSessionId) {
        Optional<Object> updatedUser = sessionRegistry.getAllPrincipals().stream()
                .filter(principalObj -> {
                    UserDetails principal = (UserDetails) principalObj;
                    return userEmail.equals(principal.getUsername());
                })
                .findFirst();
        if (updatedUser.isPresent()) {
            sessionRegistry.getAllSessions(updatedUser.get(), false).stream().filter(session -> session.getSessionId() != specificSessionId).collect(Collectors.toList()).forEach(SessionInformation::expireNow);
        }
    }

}
