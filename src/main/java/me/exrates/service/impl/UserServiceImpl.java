package me.exrates.service.impl;


import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserDao;
import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.Email;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.service.*;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.UniqueEmailConstraintException;
import me.exrates.service.exception.api.UniqueNicknameConstraintException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.token.TokenScheduler;
import org.apache.commons.lang3.tuple.Pair;
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
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Exrates";

    private final int USER_FILES_THRESHOLD = 3;

    private final int USER_2FA_NOTIFY_DAYS = 6;

    private final Set<String> USER_ROLES = Stream.of(UserRole.values()).map(UserRole::name).collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private final static List<String> LOCALES_LIST = new ArrayList<String>() {{
        add("EN");
        add("RU");
        add("CN");
        add("ID");
        add("AR");
    }};

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserSessionService userSessionService;
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
    @Autowired
    private NotificationsSettingsService settingsService;
    @Autowired
    private G2faService g2faService;
    @Autowired
    private ExchangeApi exchangeApi;
    @Autowired
    private UserSettingService userSettingService;

    private Cache<String, UsersInfoDto> usersInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public List<String> getLocalesList() {
        return LOCALES_LIST;
    }

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


    @Override
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

    @Override
    public int getIdByNickname(String nickname) {
        return userDao.getIdByNickname(nickname);
    }


    public boolean setNickname(String newNickName, String userEmail) {
        return userDao.setNickname(newNickName, userEmail);
    }

    @Override
    public boolean hasNickname(String userEmail) {
        String nickname = userDao.findByEmail(userEmail).getNickname();
        if (nickname == null || nickname.trim().length() == 0) {
            return false;
        } else return true;
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User findByNickname(String nickname) {
        return userDao.findByNickname(nickname);
    }

    @Override
    public void createUserFile(final int userId, final List<Path> paths) {
        if (findUserDoc(userId).size() == USER_FILES_THRESHOLD) {
            throw new IllegalStateException("User (id:" + userId + ") can not have more than 3 docs on the server ");
        }
        userDao.createUserDoc(userId, paths);
    }

    @Override
    public void setUserAvatar(final int userId, Path path) {
        userDao.setUserAvatar(userId, path.toString());
    }

    @Override
    public void deleteUserFile(final int docId) {
        userDao.deleteUserDoc(docId);
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        return userDao.findUserDoc(userId);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        return userDao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        return userDao.ifEmailIsUnique(email);
    }

    public String logIP(String email, String host) {
        int id = userDao.getIdByEmail(email);
        String userIP = userDao.getIP(id);
        if (userIP == null) {
            userDao.setIP(id, host);
        }
        userDao.addIPToLog(id, host);
        return userIP;
    }

    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();

    }

    public List<UserRole> getAllRoles() {
        return userDao.getAllRoles();
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

    @Override
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

    @Override
    public boolean update(UpdateUserDto user, Locale locale) {
        return update(user, false, locale);
    }


    @Override
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        sendEmailWithToken(user, tokenType, tokenLink, emailSubject, emailText, locale, null);
    }

    @Override
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

    @Override
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

    @Override
    public User getCommonReferralRoot() {
        try {
            return userDao.getCommonReferralRoot();
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
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

    @Override
    public void updateCommonReferralRoot(final int userId) {
        userDao.updateCommonReferralRoot(userId);
    }

    @Override
    public int setCallbackURL(final int userId, final CallbackURL callbackURL) throws CallBackUrlAlreadyExistException {
        if (!Strings.isNullOrEmpty(userSettingService.getCallbackURL(userId, callbackURL.getPairId()))) {
            throw new CallBackUrlAlreadyExistException("Callback already present");
        }
        return userSettingService.addCallbackURL(userId, callbackURL);
    }

    @Override
    public int updateCallbackURL(final int userId, final CallbackURL callbackURL) {
        return userSettingService.updateCallbackURL(userId, callbackURL);
    }

    @Override
    public String getCallBackUrlByEmail(String email, Integer currencyPairId) {
        return userSettingService.getCallbackURL(getIdByEmail(email), currencyPairId);
    }

    @Override
    public String getPreferedLang(int userId) {
        return userDao.getPreferredLang(userId);
    }

    @Override
    public String getPreferedLangByEmail(String email) {
        return userDao.getPreferredLangByEmail(email);
    }

    @Override
    public boolean setPreferedLang(int userId, Locale locale) {
        return userDao.setPreferredLang(userId, locale);
    }

    @Override
    public boolean insertIp(String email, String ip) {
        return userDao.insertIp(email, ip);
    }

    @Override
    public UserIpDto getUserIpState(String email, String ip) {
        return userDao.getUserIpState(email, ip);
    }

    @Override
    public boolean setLastRegistrationDate(int userId, String ip) {
        return userDao.setLastRegistrationDate(userId, ip);
    }

    @Override
    public void saveTemporaryPasswordAndNotify(UpdateUserDto user, String temporaryPass, Locale locale) {
        user.setStatus(UserStatus.REGISTERED);
        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/rest/user/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale, temporaryPass);
        }
    }

    @Override
    public boolean replaceUserPassAndDelete(String token, Long tempPassId) {
        TemporalToken temporalToken = userDao.verifyToken(token);

        if (temporalToken != null) {
            TemporaryPasswordDto dto = userDao.getTemporaryPasswordById(tempPassId);
            LOGGER.debug(dto);
            if (LocalDateTime.now().isAfter(dto.getDateCreation().plusDays(1L))) {
                removeTemporaryPassword(dto.getId());
                throw new ResetPasswordExpirationException("Password expired");
            }
            userDao.updateUserPasswordFromTemporary(tempPassId);
            removeTemporaryPassword(tempPassId);

            userSessionService.invalidateUserSessionExceptSpecific(userDao.getUserById(dto.getUserId()).getEmail(), null);

            return deleteTokensAndUpdateUser(temporalToken) > 0;
        }
        removeTemporaryPassword(tempPassId);
        throw new TokenNotFoundException("Cannot find token");


    }

    @Override
    public boolean removeTemporaryPassword(Long id) {
        return userDao.deleteTemporaryPassword(id);
    }

    @Override
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

    @Override
    public List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails) {
        try {
            List<UserSessionInfoDto> list = userDao.getUserSessionInfo(emails);
            log.debug(Arrays.toString(list.toArray()));
            return list;
        } catch (Exception e) {
            log.error(e);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getAvatarPath(Integer userId) {
        return userDao.getAvatarPath(userId);
    }

    @Override
    public Locale getUserLocaleForMobile(String email) {
        String lang = getPreferedLangByEmail(email);
        //adaptation for locales available in mobile app
        if (!("ru".equalsIgnoreCase(lang) || "en".equalsIgnoreCase(lang))) {
            lang = "en";
        }

        return new Locale(lang);
    }

    @Override
    public Collection<Comment> getUserComments(int id, String authenticatedAdminEmail) {
        User admin = findByEmail(authenticatedAdminEmail);
        Collection<Comment> comments = userDao.getUserComments(id);
        comments.forEach(comment -> comment.setEditable(isCommentEditable(admin.getId()).test(comment)));
        return comments;

    }

    private Predicate<Comment> isCommentEditable(int authenticatedAdminId) {
        return comment -> LocalDateTime.now().isBefore(comment.getCreationTime().plusHours(24L)) &&
                comment.getCreator().getId() == authenticatedAdminId && !comment.isMessageSent();
    }

    @Override
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

    @Override
    public void editUserComment(int commentId, String newComment, String email, boolean sendMessage, String authenticatedAdminEmail) {
        Comment comment = userDao.getCommentById(commentId).orElseThrow(() -> new UserCommentNotFoundException("id " + commentId));
        User admin = findByEmail(authenticatedAdminEmail);
        if (isCommentEditable(admin.getId()).test(comment)) {
            userDao.editUserComment(commentId, newComment, sendMessage);
            if (sendMessage) {
                notificationService.notifyUser(comment.getUser().getId(), NotificationEvent.ADMIN, "admin.subjectCommentTitle",
                        "admin.subjectCommentMessage", new Object[]{": " + newComment});
            }
        } else {
            throw new CommentNonEditableException();
        }
    }

    @Override
    public boolean deleteUserComment(int id) {
        return userDao.deleteUserComment(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale) {
        return userDao.getAuthorityOptionsForUser(userId).stream()
                .filter(option -> allowedAuthorities.contains(option.getAdminAuthority().name()))
                .peek(option -> option.localize(messageSource, locale))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getActiveAuthorityOptionsForUser(Integer userId) {
        return userDao.getAuthorityOptionsForUser(userId).stream()
                .filter(AdminAuthorityOption::getEnabled)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId, String currentUserEmail) {
        UserRole currentUserRole = userDao.getUserRoles(currentUserEmail);
        UserRole updatedUserRole = userDao.getUserRoleById(userId);
        if (currentUserRole != UserRole.ADMINISTRATOR && updatedUserRole == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Status modification not permitted");
        }
        userDao.updateAdminAuthorities(options, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findNicknamesByPart(String part) {
        Integer nicknameLimit = userDao.retrieveNicknameSearchLimit();
        return userDao.findNicknamesByPart(part, nicknameLimit);

    }

    @Override
    public UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String grantedAuthority = authentication.getAuthorities().
                stream().map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst().orElse(ROLE_DEFAULT_COMMISSION.name());
        LOGGER.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }

    @Override
    @Transactional
    public void setCurrencyPermissionsByUserId(List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList) {
        Integer userId = userCurrencyOperationPermissionDtoList.get(0).getUserId();
        userDao.setCurrencyPermissionsByUserId(
                userId,
                userCurrencyOperationPermissionDtoList.stream()
                        .filter(e -> e.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE)
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
            Integer userId,
            Integer currencyId,
            InvoiceOperationDirection invoiceOperationDirection) {
        return userDao.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(userId, currencyId, invoiceOperationDirection);
    }

    @Override
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    @Override
    public UserRole getUserRoleFromDB(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    @Override
    @Transactional
    public UserRole getUserRoleFromDB(Integer userId) {
        return userDao.getUserRoleById(userId);
    }

    @Transactional
    @Override
    public String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event) {
        String pin = String.valueOf(10000000 + new Random().nextInt(90000000));
        userDao.updatePinByUserEmail(userEmail, passwordEncoder.encode(pin), event);
        return pin;
    }


    /*todo refator it*/
    @Override
    public boolean checkPin(String email, String pin, NotificationMessageEventEnum event) {
        int userId = getIdByEmail(email);
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if (setting == null || setting.getNotificatorId() == null) {
            setting = NotificationsUserSetting.builder()
                    .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                    .userId(userId)
                    .notificationMessageEventEnum(event)
                    .build();
        }
        if (setting.getNotificatorId().equals(NotificationTypeEnum.GOOGLE2FA.getCode())) {
            return g2faService.checkGoogle2faVerifyCode(pin, userId);
        }
        return passwordEncoder.matches(pin, getPinForEvent(email, event));
    }

    private String getPinForEvent(String email, NotificationMessageEventEnum event) {
        return userDao.getPinByEmailAndEvent(email, event);
    }

    @Override
    public boolean isLogin2faUsed(String email) {
        return g2faService.isGoogleAuthenticatorEnable(userDao.getIdByEmail(email));
    }

    @Override
    public boolean checkIsNotifyUserAbout2fa(String email) {
        return userDao.updateLast2faNotifyDate(email);
    }

    @Override
    public List<UserIpReportDto> getUserIpReportForRoles(List<Integer> roleIds) {
        return userDao.getUserIpReportByRoleList(roleIds);
    }

    @Override
    public UsersInfoDto getUsersInfoFromCache(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles) {
        String startTimeString = startTime.toString();
        String endTimeString = endTime.toString();
        final String rolesString = userRoles.stream().map(UserRole::getName).collect(joining("-"));

        String key = String.join("/", startTimeString, endTimeString, rolesString);

        try {
            return usersInfoCache.get(key, () -> this.getUsersInfoFromDatabase(startTime, endTime, userRoles));
        } catch (ExecutionException ex) {
            log.warn("Problem with return users info");
            return UsersInfoDto.builder()
                    .newUsers(0)
                    .allUsers(0)
                    .activeUsers(0)
                    .notZeroBalanceUsers(0)
                    .oneOrMoreSuccessInputUsers(0)
                    .oneOrMoreSuccessOutputUsers(0)
                    .build();
        }

    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public UsersInfoDto getUsersInfoFromDatabase(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles) {
        UsersInfoDto usersInfo = userDao.getUsersInfo(startTime, endTime, userRoles);

        Map<Integer, List<UserBalancesDto>> usersBalances = userDao.getUserBalances(userRoles).stream()
                .collect(Collectors.groupingBy(UserBalancesDto::getUserId));

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return usersInfo.toBuilder()
                .notZeroBalanceUsers((int) usersBalances.entrySet().stream()
                        .filter(entry -> {
                            double sum = entry.getValue().stream().mapToDouble(balance -> {
                                final String currencyName = balance.getCurrencyName();
                                double activeBalance = balance.getActiveBalance().doubleValue();
                                double reservedBalance = balance.getReservedBalance().doubleValue();

                                Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currencyName);
                                if (isNull(ratePair)) {
                                    ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
                                }
                                final double usdRate = ratePair.getLeft().doubleValue();

                                return (activeBalance + reservedBalance) * usdRate;
                            }).sum();

                            return sum >= 1;
                        })
                        .count())
                .build();
    }


    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
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

    public User getUserByTemporalToken(String token) {
        return userDao.getUserByTemporalToken(token);
    }

    @Override
    public boolean checkPassword(int userId, String password) {
        return passwordEncoder.matches(password, userDao.getPassword(userId));
    }

    @Override
    public long countUserIps(String userEmail) {
        return userDao.countUserEntrance(userEmail);
    }


}