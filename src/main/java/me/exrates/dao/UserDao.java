package me.exrates.dao;

import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public interface UserDao {

    int getIdByNickname(String nickname);

    boolean setNickname(String newNickName, String userEmail);

    boolean create(User user);

    void createUserDoc(int userId, List<Path> paths);

    void setUserAvatar(int userId, String path);

    List<UserFile> findUserDoc(int userId);

    void deleteUserDoc(int docId);

    List<UserRole> getAllRoles();

    UserRole getUserRoleById(Integer id);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId);

    boolean createAdminAuthoritiesForUser(Integer userId, UserRole role);

    boolean hasAdminAuthorities(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId);

    boolean removeUserAuthorities(Integer userId);


    User findByEmail(String email);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String getIP(int userId);

    boolean setIP(int id, String ip);

    int getIdByEmail(String email);

    boolean addIPToLog(int userId, String ip);

    boolean update(UpdateUserDto user);

    User findByNickname(String nickname);

    User getUserById(int id);

    User getCommonReferralRoot();

    void updateCommonReferralRoot(int userId);

    UserRole getUserRoles(String email);

    boolean createTemporalToken(TemporalToken token);

    TemporalToken verifyToken(String token);

    boolean deleteTemporalToken(TemporalToken token);

    /**
     * Delete all tokens for user with concrete TokenType.
     * Uses in "Send again" in registration.
     *
     * @param token (TemporalToken)
     * @return boolean (false/true)
     */
    boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token);

    List<TemporalToken> getTokenByUserAndType(int userId, TokenType tokenType);

    List<TemporalToken> getAllTokens();

    boolean delete(User user);

    String getPreferredLang(int userId);

    boolean setPreferredLang(int userId, Locale locale);

    String getPreferredLangByEmail(String email);

    boolean insertIp(String email, String ip);

    UserIpDto getUserIpState(String email, String ip);

    boolean setIpStateConfirmed(int userId, String ip);

    boolean setLastRegistrationDate(int userId, String ip);

    Long saveTemporaryPassword(Integer userId, String password, Integer tokenId);

    TemporaryPasswordDto getTemporaryPasswordById(Long id);

    boolean updateUserPasswordFromTemporary(Long tempPassId);

    boolean deleteTemporaryPassword(Long id);

    boolean tempDeleteUser(int id);

    boolean tempDeleteUserWallets(int userId);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    String getAvatarPath(Integer userId);

    Collection<Comment> getUserComments(int id);

    Optional<Comment> getCommentById(int id);

    boolean addUserComment(Comment comment);

    void editUserComment(int id, String newComment, boolean sendMessage);

    boolean deleteUserComment(int id);

    Integer retrieveNicknameSearchLimit();

    List<String> findNicknamesByPart(String part, Integer limit);

    void setCurrencyPermissionsByUserId(Integer userId, List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList);

    InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(Integer userId, Integer currencyId, InvoiceOperationDirection invoiceOperationDirection);

    String getEmailById(Integer id);

    UserRole getUserRoleByEmail(String email);

    boolean updateLast2faNotifyDate(String email);

    List<UserIpReportDto> getUserIpReportByRoleList(List<Integer> userRoleList);

    String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event);

    void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event);

    UsersInfoDto getUsersInfo(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    List<UserBalancesDto> getUserBalances(List<UserRole> userRoles);

    User getUserByTemporalToken(String token);

    String getPassword(int userId);

    long countUserEntrance(String email);
}
