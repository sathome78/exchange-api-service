package me.exrates.openapi.service;


import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.UserDao;
import me.exrates.openapi.exceptions.AbsentFinPasswordException;
import me.exrates.openapi.exceptions.AuthenticationNotAvailableException;
import me.exrates.openapi.exceptions.NotConfirmedFinPasswordException;
import me.exrates.openapi.exceptions.UnRegisteredUserDeleteException;
import me.exrates.openapi.exceptions.WrongFinPasswordException;
import me.exrates.openapi.exceptions.api.UniqueEmailConstraintException;
import me.exrates.openapi.exceptions.api.UniqueNicknameConstraintException;
import me.exrates.openapi.model.AdminAuthorityOption;
import me.exrates.openapi.model.Comment;
import me.exrates.openapi.model.Email;
import me.exrates.openapi.model.TemporalToken;
import me.exrates.openapi.model.User;
import me.exrates.openapi.model.UserFile;
import me.exrates.openapi.model.dto.UpdateUserDto;
import me.exrates.openapi.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.openapi.model.enums.NotificationEvent;
import me.exrates.openapi.model.enums.NotificationMessageEventEnum;
import me.exrates.openapi.model.enums.TokenType;
import me.exrates.openapi.model.enums.UserCommentTopicEnum;
import me.exrates.openapi.model.enums.UserRole;
import me.exrates.openapi.model.enums.UserStatus;
import me.exrates.openapi.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.openapi.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.openapi.service.token.TokenScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenScheduler tokenScheduler;

    @Autowired
    private ReferralService referralService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Exrates";

    private final int USER_FILES_THRESHOLD = 3;

    private final Set<String> USER_ROLES = Stream.of(UserRole.values()).map(UserRole::name).collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale, String source) {
        Boolean flag = false;
        if (this.ifEmailIsUnique(user.getEmail())) {
            if (this.ifNicknameIsUnique(user.getNickname())) {
                if (userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp())) {
                    int user_id = this.getIdByEmail(user.getEmail());
                    user.setId(user_id);
                    if (source != null && !source.isEmpty()) {
                        String view = "view=" + source;
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale, null, view);
                    } else {
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
                    }
                    flag = true;
                }
            }
        }
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUserRest(User user, Locale locale) {
        if (!ifNicknameIsUnique(user.getNickname())) {
            LOGGER.error("Nickname already exists!");
            throw new UniqueNicknameConstraintException("Nickname already exists!");
        }
        if (!ifEmailIsUnique(user.getEmail())) {
            LOGGER.error("Email already exists!");
            throw new UniqueEmailConstraintException("Email already exists!");
        }
        Boolean result = userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp());
        if (result) {
            int user_id = this.getIdByEmail(user.getEmail());
            user.setId(user_id);
            userDao.setPreferredLang(user_id, locale);
            sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
        }
        return result;
    }

    /**
     * Verifies user by token that obtained by the redirection from email letter
     * if the verifying is success, all token corresponding type of this user will be deleted
     * if there are jobs for deleted tokens in scheduler, they will be deleted from queue.
     */
    @Transactional(rollbackFor = Exception.class)
    public int verifyUserEmail(String token) {
        TemporalToken temporalToken = userDao.verifyToken(token);
        //deleting all tokens related with current through userId and tokenType
        return temporalToken != null ? deleteTokensAndUpdateUser(temporalToken) : 0;
    }

    private int deleteTokensAndUpdateUser(TemporalToken temporalToken) {
        if (userDao.deleteTemporalTokensOfTokentypeForUser(temporalToken)) {
            //deleting of appropriate jobs
            tokenScheduler.deleteJobsRelatedWithToken(temporalToken);
            /**/
            if (temporalToken.getTokenType() == TokenType.CONFIRM_NEW_IP) {
                if (!userDao.setIpStateConfirmed(temporalToken.getUserId(), temporalToken.getCheckIp())) {
                    return 0;
                }
            }
        }
        return temporalToken.getUserId();
    }

    /*
     * for checking if there are open tokens of concrete type for the user
     * */
    public List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType) {
        return userDao.getTokenByUserAndType(user.getId(), tokenType);
    }

    public List<TemporalToken> getAllTokens() {
        return userDao.getAllTokens();
    }

    /*
     * deletes only concrete token
     * */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException {
        boolean result = false;
        TemporalToken temporalToken = userDao.verifyToken(token);
        result = userDao.deleteTemporalToken(temporalToken);
        if (temporalToken.getTokenType() == TokenType.REGISTRATION) {
            User user = userDao.getUserById(temporalToken.getUserId());
            if (user.getStatus() == UserStatus.REGISTERED) {
                LOGGER.debug(String.format("DELETING USER %s", user.getEmail()));
                referralService.updateReferralParentForChildren(user);
                result = userDao.delete(user);
                if (!result) {
                    throw new UnRegisteredUserDeleteException();
                }
            }
        }
        return result;
    }

    public int getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    public int getIdByNickname(String nickname) {
        return userDao.getIdByNickname(nickname);
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User findByNickname(String nickname) {
        return userDao.findByNickname(nickname);
    }

    public void createUserFile(final int userId, final List<Path> paths) {
        if (findUserDoc(userId).size() == USER_FILES_THRESHOLD) {
            throw new IllegalStateException("User (id:" + userId + ") can not have more than 3 docs on the server ");
        }
        userDao.createUserDoc(userId, paths);
    }

    public void setUserAvatar(final int userId, Path path) {
        userDao.setUserAvatar(userId, path.toString());
    }

    public List<UserFile> findUserDoc(final int userId) {
        return userDao.findUserDoc(userId);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        return userDao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        return userDao.ifEmailIsUnique(email);
    }

    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();

    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUserByAdmin(User user) {
        boolean result = userDao.create(user);
        if (result && user.getRole() != UserRole.USER && user.getRole() != UserRole.ROLE_CHANGE_PASSWORD) {
            return userDao.createAdminAuthoritiesForUser(userDao.getIdByEmail(user.getEmail()), user.getRole());
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserByAdmin(UpdateUserDto user) {
        boolean result = userDao.update(user);
        if (result) {
            boolean hasAdminAuthorities = userDao.hasAdminAuthorities(user.getId());
            if (user.getRole() == UserRole.USER && hasAdminAuthorities) {
                return userDao.removeUserAuthorities(user.getId());
            }
            if (!hasAdminAuthorities && user.getRole() != null &&
                    user.getRole() != UserRole.USER && user.getRole() != UserRole.ROLE_CHANGE_PASSWORD) {
                return userDao.createAdminAuthoritiesForUser(user.getId(), user.getRole());
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserEntryDay(String userEmail) {
        Integer userId = userDao.getIdByEmail(userEmail);
        if (userId != null) {
            return userDao.createUserEntryDay(userId, LocalDateTime.now());
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserSettings(UpdateUserDto user) {
        return userDao.update(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(UpdateUserDto user, boolean resetPassword, Locale locale) {
        boolean changePassword = user.getPassword() != null && !user.getPassword().isEmpty();
        boolean changeFinPassword = user.getFinpassword() != null && !user.getFinpassword().isEmpty();

        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            if (changePassword) {
                sendUnfamiliarIpNotificationEmail(u, "admin.changePasswordTitle", "user.settings.changePassword.successful", locale);
            } else if (changeFinPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_FIN_PASSWORD, "/changeFinPasswordConfirm", "emailsubmitChangeFinPassword.subject", "emailsubmitChangeFinPassword.text", locale);
            } else if (resetPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale);
            }
        }
        return true;
    }

    public boolean update(UpdateUserDto user, Locale locale) {
        return update(user, false, locale);
    }

    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        sendEmailWithToken(user, tokenType, tokenLink, emailSubject, emailText, locale, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String tempPass, String... params) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());
        token.setAlreadyUsed(false);

        createTemporalToken(token);
        String tempPassId = "";
        if (tempPass != null) {
            tempPassId = "&tempId=" + userDao.saveTemporaryPassword(user.getId(), tempPass, userDao.verifyToken(token.getValue()).getId());
        }

        Email email = new Email();
        StringBuilder confirmationUrl = new StringBuilder(tokenLink + "?token=" + token.getValue() + tempPassId);
        if (tokenLink.equals("/resetPasswordConfirm")) {
            confirmationUrl.append("&email=").append(user.getEmail());
        }
        String rootUrl = "";
        if (!confirmationUrl.toString().contains("//")) {
            rootUrl = request.getScheme() + "://" + request.getServerName() +
                    ":" + request.getServerPort();
        }
        if (params != null) {
            for (String patram : params) {
                confirmationUrl.append("&").append(patram);
            }
        }
        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        rootUrl +
                        confirmationUrl.toString() +
                        "'>" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));

        email.setTo(user.getEmail());
        if (tokenType.equals(TokenType.REGISTRATION)
                || tokenType.equals(TokenType.CHANGE_PASSWORD)
                || tokenType.equals(TokenType.CHANGE_FIN_PASSWORD)) {
            sendMailService.sendMailMandrill(email);
        } else {
            sendMailService.sendMail(email);
        }
    }

    public void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale) {
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setMessage(messageSource.getMessage(emailText, new Object[]{user.getIp()}, locale));
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        sendMailService.sendInfoMail(email);
    }

    public boolean createTemporalToken(TemporalToken token) {
        log.info("Token is " + token);
        boolean result = userDao.createTemporalToken(token);
        if (result) {
            log.info("Token succesfully saved");
            tokenScheduler.initTrigers();
        }
        return result;
    }

    public User getCommonReferralRoot() {
        try {
            return userDao.getCommonReferralRoot();
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
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

    public String getPreferedLang(int userId) {
        return userDao.getPreferredLang(userId);
    }

    public String getPreferedLangByEmail(String email) {
        return userDao.getPreferredLangByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tempDeleteUser(String email) {
        int id = userDao.getIdByEmail(email);
        LOGGER.debug(id);
        boolean result = userDao.tempDeleteUserWallets(id) && userDao.tempDeleteUser(id);
        if (!result) {
            throw new RuntimeException("Could not delete");
        }
        return result;
    }

    @PostConstruct
    private void initTokenTriggers() {
        tokenScheduler.initTrigers();
    }

    public Locale getUserLocaleForMobile(String email) {
        String lang = getPreferedLangByEmail(email);
        //adaptation for locales available in mobile app
        if (!("ru".equalsIgnoreCase(lang) || "en".equalsIgnoreCase(lang))) {
            lang = "en";
        }
        return new Locale(lang);
    }

    public boolean addUserComment(UserCommentTopicEnum topic, String newComment, String email, boolean sendMessage) {

        User user = findByEmail(email);
        User creator;
        Comment comment = new Comment();
        comment.setMessageSent(sendMessage);
        comment.setUser(user);
        comment.setComment(newComment);
        comment.setUserCommentTopic(topic);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            creator = findByEmail(auth.getName());
            comment.setCreator(creator);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        boolean success = userDao.addUserComment(comment);

        if (comment.isMessageSent()) {
            notificationService.notifyUser(user.getId(), NotificationEvent.ADMIN, "admin.subjectCommentTitle",
                    "admin.subjectCommentMessage", new Object[]{": " + newComment});
        }

        return success;
    }

    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale) {
        return userDao.getAuthorityOptionsForUser(userId).stream()
                .filter(option -> allowedAuthorities.contains(option.getAdminAuthority().name()))
                .peek(option -> option.localize(messageSource, locale))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getActiveAuthorityOptionsForUser(Integer userId) {
        return userDao.getAuthorityOptionsForUser(userId).stream()
                .filter(AdminAuthorityOption::getEnabled)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findNicknamesByPart(String part) {
        Integer nicknameLimit = userDao.retrieveNicknameSearchLimit();
        return userDao.findNicknamesByPart(part, nicknameLimit);
    }

    public UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String grantedAuthority = authentication.getAuthorities().
                stream().map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst().orElse(ROLE_DEFAULT_COMMISSION.name());
        LOGGER.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }

    @Transactional
    public void setCurrencyPermissionsByUserId(List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList) {
        Integer userId = userCurrencyOperationPermissionDtoList.get(0).getUserId();
        userDao.setCurrencyPermissionsByUserId(
                userId,
                userCurrencyOperationPermissionDtoList.stream()
                        .filter(e -> e.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE)
                        .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
            Integer userId,
            Integer currencyId,
            InvoiceOperationDirection invoiceOperationDirection) {
        return userDao.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(userId, currencyId, invoiceOperationDirection);
    }

    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    public UserRole getUserRoleFromDB(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    @Transactional
    public UserRole getUserRoleFromDB(Integer userId) {
        return userDao.getUserRoleById(userId);
    }

    @Transactional
    public String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event) {
        String pin = String.valueOf(10000000 + new Random().nextInt(90000000));
        userDao.updatePinByUserEmail(userEmail, passwordEncoder.encode(pin), event);
        return pin;
    }

    @Transactional
    public String generateQRUrl(String userEmail) throws UnsupportedEncodingException {
        String secret2faCode = userDao.get2faSecretByEmail(userEmail);
        if (secret2faCode == null || secret2faCode.isEmpty()) {
            userDao.set2faSecretCode(userEmail);
            secret2faCode = userDao.get2faSecretByEmail(userEmail);
        }
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, userEmail, secret2faCode, APP_NAME), "UTF-8");
    }

    public String getUserEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthenticationNotAvailableException();
        }
        return auth.getName();
    }

    @Transactional(rollbackFor = Exception.class)
    public TemporalToken verifyUserEmailForForgetPassword(String token) {
        return userDao.verifyToken(token);
    }
}