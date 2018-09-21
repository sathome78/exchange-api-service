package me.exrates.openapi.service;

import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.openapi.dao.TransactionDao;
import me.exrates.openapi.service.persistence.dao.TransactionDao;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class TransactionService {

  private static final Logger LOG = LogManager.getLogger(TransactionService.class);
  private static final int decimalPlaces = 8;


  @Autowired
  private TransactionDao transactionDao;
  @Autowired
  private WalletService walletService;
  @Autowired
  private CompanyWalletService companyWalletService;
  @Autowired
  private UserService userService;
  @Autowired
  private OrderService orderService;
  @Autowired
  private MerchantService merchantService;
  @Autowired
  private CurrencyService currencyService;

  @Transactional(propagation = Propagation.MANDATORY)
  public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
    final Currency currency = creditsOperation.getCurrency();
    final User user = creditsOperation.getUser();
    final String currencyName = currency.getName();

    CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
    companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

    Wallet userWallet = walletService.findByUserAndCurrency(user, currency);
    userWallet = userWallet == null ? walletService.create(user, currency) : userWallet;

    Transaction transaction = new Transaction();
    transaction.setAmount(creditsOperation.getAmount());
    transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
    transaction.setCommission(creditsOperation.getCommission());
    transaction.setCompanyWallet(companyWallet);
    transaction.setUserWallet(userWallet);
    transaction.setCurrency(currency);
    transaction.setDatetime(LocalDateTime.now());
    transaction.setMerchant(creditsOperation.getMerchant());
    transaction.setOperationType(creditsOperation.getOperationType());
    transaction.setProvided(false);
    transaction.setConfirmation((currencyName).equals("BTC") ? -1 : -1);
    transaction.setSourceType(creditsOperation.getTransactionSourceType());
    transaction = transactionDao.create(transaction);
    if (transaction == null) {
      throw new TransactionPersistException("Failed to provide transaction ");
    }
    LOG.info("Transaction created:" + transaction);
    return transaction;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Transaction findById(int id) {
    return transactionDao.findById(id);
  }

  public void updateTransactionAmount(Transaction transaction) {
    updateAmount(transaction, transaction.getAmount());
  }

  public void updateTransactionAmount(final Transaction transaction, final BigDecimal amount) {
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalArgumentException("Updating amount only available for INPUT operation");
    }
    updateAmount(transaction, amount);
  }

  public BigDecimal calculateNewCommission(Transaction transaction, BigDecimal amount) {
    return calculateCommissionFromAmpunt(amount, transaction.getCommission().getValue(),
        currencyService.resolvePrecision(transaction.getCurrency().getName()));
  }

  private void updateAmount(Transaction transaction, BigDecimal amount) {
    int scale = currencyService.resolvePrecision(transaction.getCurrency().getName());
    BigDecimal commissionRate = transaction.getCommission().getValue();
    BigDecimal commission = calculateCommissionFromAmpunt(amount, commissionRate, scale);
    final BigDecimal newAmount = amount.subtract(commission).setScale(scale, ROUND_HALF_UP);
    transaction.setCommissionAmount(commission);
    transaction.setAmount(newAmount);
    transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
  }

  private BigDecimal calculateCommissionFromAmpunt(BigDecimal amount, BigDecimal commissionRate, int scale) {
    BigDecimal mass = BigDecimal.valueOf(100L).add(commissionRate);
    return amount.multiply(commissionRate)
        .divide(mass, scale, ROUND_HALF_UP).setScale(scale, ROUND_HALF_UP);
  }

  public void nullifyTransactionAmountForWithdraw(final Transaction transaction) {
    if (transaction.getOperationType() != OperationType.OUTPUT) {
      throw new IllegalArgumentException("Nullifying amount only available for OUTPUT operations");
    }
    updateAmount(transaction, BigDecimal.ZERO);
  }

  public void updateTransactionConfirmation(final int transactionId, final int confirmations) {
    transactionDao.updateTransactionConfirmations(transactionId, confirmations);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void provideTransaction(Transaction transaction) {
    switch (transaction.getOperationType()) {
      case INPUT:
        walletService.depositActiveBalance(transaction.getUserWallet(), transaction.getAmount());
        companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
        break;
      case OUTPUT:
        walletService.withdrawReservedBalance(transaction.getUserWallet(), transaction.getAmount().add(transaction.getCommissionAmount()));
        companyWalletService.withdraw(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
        break;
    }
    if (!transactionDao.provide(transaction.getId())) {
      throw new TransactionProvidingException("Failed to provide transaction #" + transaction.getId());
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void invalidateTransaction(Transaction transaction) {
    if (!transactionDao.delete(transaction.getId())) {
      throw new TransactionProvidingException("Failed to delete transaction #" + transaction.getId());
    }
  }

  public DataTable<List<OperationViewDto>> showUserOperationHistory(
          Integer requesterUserId,
          Integer userId,
          AdminTransactionsFilterData filterData, DataTableParams dataTableParams, Locale locale) {
    requesterUserId = requesterUserId.equals(userId) ? null : requesterUserId;
    final List<Integer> wallets = walletService.getAllWallets(userId).stream()
            .mapToInt(Wallet::getId)
            .boxed()
            .collect(Collectors.toList());
    final DataTable<List<OperationViewDto>> result = new DataTable<>();
    final PagingData<List<Transaction>> transactions = transactionDao.findAllByUserWallets(requesterUserId, wallets,
            filterData, dataTableParams, locale);
    final List<OperationViewDto> operationViews = new ArrayList<>();
    for (final Transaction t : transactions.getData()) {
      OperationViewDto view = new OperationViewDto();
      view.setDatetime(t.getDatetime());
      view.setAmount(t.getAmount());
      view.setCommissionAmount(t.getCommissionAmount());
      view.setCurrency(t.getCurrency().getName());
      view.setOrder(t.getOrder());
      view.setStatus(merchantService.resolveTransactionStatus(t, locale));
      view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(t.getSourceType(), t.getOperationType(), t.getAmount()));
      view.setMerchant(t.getMerchant() == null ? new Merchant(0, null, t.getSourceType().name()) : t.getMerchant());
      view.setSourceType(t.getSourceType().name());
      view.setSourceId(t.getSourceId());
      setTransactionMerchantAndOrder(view, t);
      operationViews.add(view);
    }
    result.setData(operationViews);
    result.setRecordsFiltered(transactions.getFiltered());
    result.setRecordsTotal(transactions.getTotal());
    return result;
  }

  private void setTransactionMerchantAndOrder(OperationViewDto view, Transaction transaction) {
    TransactionSourceType sourceType = transaction.getSourceType();
    OperationType operationType = transaction.getOperationType();
    BigDecimal amount = transaction.getAmount();
    view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(sourceType, operationType, amount));
    if (sourceType == TransactionSourceType.REFILL || sourceType == TransactionSourceType.WITHDRAW) {
      view.setMerchant(transaction.getMerchant());
    } else {
      view.setMerchant(new Merchant(0, sourceType.name(), sourceType.name()));
    }

  }

  public List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale) {
    List<AccountStatementDto> result = transactionDao.getAccountStatement(walletId, offset, limit, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<AccountStatementDto>() {{
        add(new AccountStatementDto(false));
      }};
    }
    return result;
  }

  public DataTable<List<AccountStatementDto>> getAccountStatementForAdmin(Integer walletId, Integer offset, Integer limit, Locale locale) {
    DataTable<List<AccountStatementDto>> result = new DataTable<>();
    int total = transactionDao.getStatementSize(walletId);
    result.setRecordsFiltered(total);
    result.setRecordsTotal(total);
    result.setData(transactionDao.getAccountStatement(walletId, offset, limit, locale)
        .stream().filter(statement -> statement.getTransactionId() > 0).collect(Collectors.toList()));
    LOG.debug(result);
    return result;
  }

  public BigDecimal maxAmount() {
    return transactionDao.maxAmount();
  }

  public BigDecimal maxCommissionAmount() {
    return transactionDao.maxCommissionAmount();
  }

  public List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale) {
    return transactionDao.getAccountStatement(walletId, offset, limit, locale);
  }

  @Transactional
  public void setSourceId(Integer trasactionId, Integer sourceId) {
    transactionDao.setSourceId(trasactionId, sourceId);
  }

  @Transactional(transactionManager = "slaveTxManager", readOnly = true)
  public List<TransactionFlatForReportDto> getAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      Integer operationType,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList) {
    return transactionDao.findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(startDate, endDate, operationType, roleIdList, currencyList, sourceTypeList);
  }

  @Transactional
  public boolean setStatusById(Integer trasactionId, Integer statusId) {
    return transactionDao.setStatusById(trasactionId, statusId);
  }

  @Transactional(transactionManager = "slaveTxManager", readOnly = true)
  public List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(
      Integer requesterUserId,
      String startDate,
      String endDate,
      List<Integer> roleIdList) {
    return transactionDao.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(requesterUserId, startDate, endDate, roleIdList);
  }

  @Transactional(transactionManager = "slaveTxManager", readOnly = true)
  public List<UserSummaryOrdersDto> getUserSummaryOrdersList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles) {
    return transactionDao.getUserSummaryOrdersList(requesterUserId, startDate, endDate, roles);
  }

  private List<String> convertTrListToString(List<OperationViewDto> transactions) {
    List<String> transactionsResult = new ArrayList<>();
    transactionsResult.add(getCSVTransactionsHeader());
    transactionsResult.add("\n");
    transactions.forEach(i -> {
      StringBuilder sb = new StringBuilder();
      sb.append(i.getDatetime())
          .append(";")
          .append(i.getOperationType())
          .append(";")
          .append(i.getStatus())
          .append(";")
          .append(i.getCurrency())
          .append(";")
          .append(BigDecimalProcessing.formatNonePoint(i.getAmount(), false))
          .append(";")
          .append(BigDecimalProcessing.formatNonePoint(i.getCommissionAmount(), false))
          .append(";")
          .append(i.getMerchant().getName())
          .append(";")
          .append(i.getOrder() != null ? i.getOrder().getId() : 0);
      transactionsResult.add(sb.toString());
      transactionsResult.add("\n");
    });
    return transactionsResult;
  }

  private String getCSVTransactionsHeader() {
    return "Date;Operation Type;Status;Currency;Amount;Comission;Merchant;Source Id";
  }

  private void setTransactionMerchant(Transaction transaction) {
    LOG.debug(transaction);
    TransactionSourceType sourceType = transaction.getSourceType();
    if (sourceType == TransactionSourceType.REFILL || sourceType == TransactionSourceType.WITHDRAW) {
      transaction.setMerchant(transaction.getMerchant());
    } else {
      transaction.setMerchant(new Merchant(0, sourceType.name(), sourceType.name()));
    }

  }

  public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
    return transactionDao.getPayedRefTransactionsByOrderId(orderId);
  }
}
