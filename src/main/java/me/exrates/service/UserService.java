package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.UnRegisteredUserDeleteException;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface UserService {

    int getIdByEmail(String email);

    int getIdByNickname(String nickname);

    boolean setNickname(String newNickName, String userEmail);

    boolean hasNickname(String userEmail);

    User findByEmail(String email);

    User findByNickname(String nickname);

    void createUserFile(int userId, List<Path> paths);

    void setUserAvatar(int userId, Path path);

    void deleteUserFile(int docId);

    List<UserFile> findUserDoc(int userId);

    List<String> getLocalesList();

    boolean create(User user, Locale locale, String source);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);

    List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType);

    @Transactional(rollbackFor = Exception.class)
    boolean createUserRest(User user, Locale locale);

    int verifyUserEmail(String token);

    List<UserRole> getAllRoles();

    User getUserById(int id);

    boolean createUserByAdmin(User user);

    boolean updateUserByAdmin(UpdateUserDto user);

    @Transactional(rollbackFor = Exception.class)
    boolean updateUserSettings(UpdateUserDto user);

    boolean update(UpdateUserDto user, boolean resetPassword, Locale locale);

    boolean update(UpdateUserDto user, Locale locale);

    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale);

    List<TemporalToken> getAllTokens();

    boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException;

    @Transactional(rollbackFor = Exception.class)
    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String newPass, String... params);

    void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale);

    boolean createTemporalToken(TemporalToken token);

    User getCommonReferralRoot();

    void checkFinPassword(String enteredFinPassword, User storedUser, Locale locale);

    void updateCommonReferralRoot(int userId);

    int setCallbackURL(int userId, CallbackURL callbackURL) throws CallBackUrlAlreadyExistException;

    int updateCallbackURL(int userId, CallbackURL callbackURL);

    /**
     * Returns preferred locale for user stored in DB
     *
     * @param userId
     * @return string mnemonic of locale
     */
    String getPreferedLang(int userId);

    String getPreferedLangByEmail(String email);

    /**
     * Stores preferred locale for user in DB
     *
     * @param userId
     * @param locale
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setPreferedLang(int userId, Locale locale);

    /**
     * Stores IP-address in DB for user. Data is stored in table USER_IP
     *
     * @param email is email the user as his identifier
     * @param ip    is IP-address
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean insertIp(String email, String ip);

    /**
     * Returns IP-address state for user
     *
     * @param email is email for search the user
     * @param ip    is IP-address for check
     * @return one of the values the enum UserIpState: CONFIRMED or NOT_CONFIRMED
     */
    public UserIpDto getUserIpState(String email, String ip);

    /**
     * Saves in DB last date for IP? when user auth successfully
     *
     * @param userId is ID the user
     * @param ip     is ip-address from which user auth
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setLastRegistrationDate(int userId, String ip);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    void saveTemporaryPasswordAndNotify(UpdateUserDto user, String temporaryPass, Locale locale);

    boolean replaceUserPassAndDelete(String token, Long tempPassId);

    boolean removeTemporaryPassword(Long id);

    @Transactional
    boolean tempDeleteUser(String email);

    String getAvatarPath(Integer userId);

    Locale getUserLocaleForMobile(String email);

    Collection<Comment> getUserComments(int id, String authenticatedAdminEmail);

    boolean addUserComment(UserCommentTopicEnum topic, String newComment, String email, boolean sendMessage);

    void editUserComment(int commentId, String newComment, String email, boolean sendMessage, String authenticatedAdminEmail);

    boolean deleteUserComment(int id);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale);

    List<AdminAuthorityOption> getActiveAuthorityOptionsForUser(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId, String currentUserEmail);

    List<String> findNicknamesByPart(String part);

    UserRole getUserRoleFromSecurityContext();

    void setCurrencyPermissionsByUserId(List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList);

    InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(Integer userId, Integer currencyId, InvoiceOperationDirection invoiceOperationDirection);

    String getEmailById(Integer id);

    UserRole getUserRoleFromDB(String email);

    UserRole getUserRoleFromDB(Integer userId);

    @Transactional
    String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event);

    boolean checkPin(String email, String pin, NotificationMessageEventEnum event);

    boolean isLogin2faUsed(String email);

    boolean checkIsNotifyUserAbout2fa(String email);

    List<UserIpReportDto> getUserIpReportForRoles(List<Integer> roleIds);

    UsersInfoDto getUsersInfoFromCache(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    UsersInfoDto getUsersInfoFromDatabase(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    String getUserEmailFromSecurityContext();

    TemporalToken verifyUserEmailForForgetPassword(String token);

    User getUserByTemporalToken(String token);

    boolean checkPassword(int userId, String password);

    long countUserIps(String userEmail);

    String getCallBackUrlByEmail(String email, Integer currencyPairId);
}
