package me.exrates.openapi.dao;

import me.exrates.openapi.model.PagingData;
import me.exrates.openapi.model.Transaction;
import me.exrates.openapi.model.dto.InOutReportDto;
import me.exrates.openapi.model.dto.TransactionFlatForReportDto;
import me.exrates.openapi.model.dto.UserSummaryDto;
import me.exrates.openapi.model.dto.UserSummaryOrdersDto;
import me.exrates.openapi.model.dto.dataTable.DataTableParams;
import me.exrates.openapi.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.openapi.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.openapi.model.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {

    Transaction create(Transaction transaction);

    boolean updateForProvided(Transaction transaction);

    Transaction findById(int id);

    PagingData<List<Transaction>> findAllByUserWallets(
            Integer requesterUserId, List<Integer> userWalletIds, AdminTransactionsFilterData filterData, DataTableParams dataTableParams);

    boolean provide(int id);

    boolean delete(int id);

    void updateTransactionAmount(int transactionId, BigDecimal amount, BigDecimal commission);

    void updateTransactionConfirmations(int transactionId, int confirmations);

    List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

    Integer getStatementSize(Integer walletId);

    BigDecimal maxAmount();

    BigDecimal maxCommissionAmount();

    void setSourceId(Integer trasactionId, Integer sourceId);

    List<TransactionFlatForReportDto> findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(String startDate, String endDate, Integer operationType, List<Integer> roleIdList, List<Integer> currencyList, List<String> sourceTypeList);

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

    List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    List<UserSummaryDto> getUsersWalletSummaryDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, String userEmail, int requesterId);

    List<UserSummaryOrdersDto> getUserSummaryOrdersDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId);
}
