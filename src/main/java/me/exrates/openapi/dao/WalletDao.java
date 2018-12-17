package me.exrates.openapi.dao;

import me.exrates.openapi.model.User;
import me.exrates.openapi.model.Wallet;
import me.exrates.openapi.model.dto.*;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.openapi.model.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.WalletTransferStatus;
import me.exrates.openapi.model.vo.WalletOperationData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface WalletDao {

    BigDecimal getWalletABalance(int walletId);

    BigDecimal getWalletRBalance(int walletId);

    int getWalletId(int userId, int currencyId);

    int createNewWallet(Wallet wallet);

    int getUserIdFromWallet(int walletId);

    List<Wallet> findAllByUser(int userId);

    List<MyWalletsStatisticsDto> getAllWalletsForUserAndCurrenciesReduced(String email, Set<Integer> currencyIds);

    List<WalletBalanceDto> getBalancesForUser(String userEmail);

    MyWalletsStatisticsApiDto getWalletShortStatistics(int walletId);

    List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId, List<Integer> withdrawEndStatusIds,
                                                               List<Integer> withdrawSuccessStatusIds,
                                                               List<Integer> refillSuccessStatusIds);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, List<Integer> withdrawStatusIds, Locale locale);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> withdrawStatusIds, Locale locale);

    List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale);

    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email);

    Wallet findByUserAndCurrency(int userId, int currencyId);

    Wallet findById(Integer walletId);

    Wallet createWallet(User user, int currencyId);

    /**
     * Returns dto with:
     * - IDs the currency
     * - IDs the wallets -
     * - balances the wallets of participants the order: user-creator, user-acceptor, company
     * - status the order
     * and blocks the order and the set wallets within current transaction
     *
     * @param orderId
     * @param userAcceptorId
     * @return dto with data
     */
    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId);

    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType);

    WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId);

    List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId);

    void addToWalletBalance(Integer walletId, BigDecimal addedAmountActive, BigDecimal addedAmountReserved);

    List<UserWalletSummaryDto> getUsersWalletsSummaryNew(Integer requesterUserId, List<Integer> roleIds);

    boolean isUserAllowedToManuallyChangeWalletBalance(int adminId, int walletHolderUserId);

    List<UserGroupBalanceDto> getWalletBalancesSummaryByGroups();

    List<UserRoleBalanceDto> getWalletBalancesSummaryByRoles(List<Integer> roleIdsList);

    int getWalletIdAndBlock(Integer userId, Integer currencyId);

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto);

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto);

    void createReservedWalletAddress(int currencyId);

    void deleteReservedWalletAddress(int id, int currencyId);

    void updateReservedWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto);

    List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId);

    List<InternalWalletBalancesDto> getWalletBalances();

    BigDecimal retrieveSummaryUSD();

    BigDecimal retrieveSummaryBTC();

    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance);
}