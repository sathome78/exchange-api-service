package me.exrates.openapi.service;

import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.User;
import me.exrates.openapi.model.Wallet;
import me.exrates.openapi.model.dto.*;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.openapi.model.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.model.enums.*;
import me.exrates.openapi.model.vo.WalletOperationData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface WalletService {

    void balanceRepresentation(Wallet wallet);

    List<Wallet> getAllWallets(int userId);

    List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId);

    int getWalletId(int userId, int currencyId);

    BigDecimal getWalletABalance(int walletId);

    BigDecimal getWalletRBalance(int walletId);

    boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck);

    int createNewWallet(Wallet wallet);

    int getUserIdFromWallet(int walletId);

    Wallet findByUserAndCurrency(User user, Currency currency);

    Wallet create(User user, Currency currency);

    void depositActiveBalance(Wallet wallet, BigDecimal sum);

    void withdrawActiveBalance(Wallet wallet, BigDecimal sum);

    void depositReservedBalance(Wallet wallet, BigDecimal sum);

    void withdrawReservedBalance(Wallet wallet, BigDecimal sum);

    /**
     * Transfers money between active balance the wallet and reserved balance the wallet
     * and creates corresponding transaction
     *
     * @param walletId   is wallet ID
     * @param amount     amount to transfer
     * @param sourceType type the operation that caused the transfer
     * @param sourceId   ID the operation in the table that corresponds to sourceType
     * @return WalletTransferStatus with detail about result
     * @author ValkSam
     */
    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId);

    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale);

    @Transactional(readOnly = true)
    MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId);

    @Transactional(readOnly = true)
    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale);

    @Transactional(readOnly = true)
    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email);

    List<WalletBalanceDto> getBalancesForUser();

    @Transactional(rollbackFor = Exception.class)
    void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount, String adminEmail);

    List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId);

    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId);

    WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType);

    @Transactional(rollbackFor = Exception.class)
    TransferDto transferCostsToUser(Integer fromUserWalletId, Integer userId, BigDecimal amount, BigDecimal comission,
                                    Locale locale, int sourceId);

    @Transactional(rollbackFor = Exception.class)
    String transferCostsToUser(Integer userId, Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                               BigDecimal comission, Locale locale, int sourceId);

    List<UserWalletSummaryDto> getUsersWalletsSummaryForPermittedCurrencyList(Integer requesterUserId, List<Integer> roleIds);

    @Transactional
    WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId);

    boolean isUserAllowedToManuallyChangeWalletBalance(String adminEmail, int walletHolderUserId);

    List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups();

    List<UserRoleTotalBalancesReportDto<UserRole>> getWalletBalancesSummaryByRoles(List<UserRole> roles);

    int getWalletIdAndBlock(Integer userId, Integer currencyId);

    List<ExternalWalletBalancesDto> getExternalWalletBalances();

    void updateExternalMainWalletBalances();

    void updateExternalReservedWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();

    void createWalletAddress(int currencyId);

    void deleteWalletAddress(int id, int currencyId, String walletAddress);

    void updateWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto, boolean isSavedAsAddress);

    List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId);

    BigDecimal retrieveSummaryUSD();

    BigDecimal retrieveSummaryBTC();

    BigDecimal getExternalReservedWalletBalance(Integer currencyId, String walletAddress);
}