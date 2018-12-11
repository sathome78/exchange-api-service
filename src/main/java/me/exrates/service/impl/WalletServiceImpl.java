package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.api.WalletsApi;
import me.exrates.service.exception.*;
import me.exrates.service.util.Cache;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Log4j2
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final int decimalPlaces = 9;

    @Autowired
    private WalletDao walletDao;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserTransferService userTransferService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ExchangeApi exchangeApi;
    @Autowired
    private WalletsApi walletsApi;

    @Override
    public void balanceRepresentation(final Wallet wallet) {
        wallet
                .setActiveBalance(wallet.getActiveBalance());
//				.setScale(currencyService.resolvePrecision(wallet.getName()), ROUND_CEILING));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<Wallet> getAllWallets(int userId) {
        final List<Wallet> wallets = walletDao.findAllByUser(userId);
        wallets.forEach(this::balanceRepresentation);
        return wallets;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId) {
        return walletDao.getAllUserWalletsForAdminDetailed(userId,
                WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList()),
                WithdrawStatusEnum.getEndStatesSet().stream().filter(InvoiceStatus::isSuccessEndStatus).map(InvoiceStatus::getCode).collect(Collectors.toList()),
                RefillStatusEnum.getEndStatesSet().stream().filter(InvoiceStatus::isSuccessEndStatus).map(InvoiceStatus::getCode).collect(Collectors.toList()));
    }


    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData,
                                                                   String email, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
        List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyWalletsDetailedDto>() {{
                add(new MyWalletsDetailedDto(false));
            }};
        }
        return result;
    }

    @Override
    public int getWalletId(int userId, int currencyId) {
        return walletDao.getWalletId(userId, currencyId);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletABalance(int walletId) {
        return walletDao.getWalletABalance(walletId);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletRBalance(int walletId) {
        return walletDao.getWalletRBalance(walletId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
        BigDecimal balance = getWalletABalance(walletId);
        boolean result = balance.compareTo(amountForCheck) >= 0;
        if (!result) {
            log.error(String.format("Not enough wallet money: wallet id %s, actual amount %s but needed %s", walletId,
                    BigDecimalProcessing.formatNonePoint(balance, false),
                    BigDecimalProcessing.formatNonePoint(amountForCheck, false)));
        }
        return result;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public int createNewWallet(Wallet wallet) {
        return walletDao.createNewWallet(wallet);
    }

    @Override
    public int getUserIdFromWallet(int walletId) {
        return walletDao.getUserIdFromWallet(walletId);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet findByUserAndCurrency(User user, Currency currency) {
        return walletDao.findByUserAndCurrency(user.getId(), currency.getId());
    }

    @Override
    public Wallet create(User user, Currency currency) {
        final Wallet wallet = walletDao.createWallet(user, currency.getId());
        wallet.setName(currency.getName());
        return wallet;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositActiveBalance(final Wallet wallet, final BigDecimal sum) {
        walletDao.addToWalletBalance(wallet.getId(), sum, BigDecimal.ZERO);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawActiveBalance(final Wallet wallet, final BigDecimal sum) {
        final BigDecimal newBalance = wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP);
        if (newBalance.compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " +
                    wallet.toString());
        }
        walletDao.addToWalletBalance(wallet.getId(), sum.negate(), BigDecimal.ZERO);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositReservedBalance(final Wallet wallet, final BigDecimal sum) {
        wallet.setActiveBalance(wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
        if (wallet.getActiveBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        walletDao.addToWalletBalance(wallet.getId(), sum.negate(), sum);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(final Wallet wallet, final BigDecimal sum) {
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
        if (wallet.getReservedBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        walletDao.addToWalletBalance(wallet.getId(), BigDecimal.ZERO, sum.negate());
    }

    @Override
    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId) {
        return walletInnerTransfer(walletId, amount, sourceType, sourceId, null);
    }

    @Override
    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
        return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
    }

    @Override
    public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
        return walletDao.walletBalanceChange(walletOperationData);
    }

    @Override
    public List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale) {
        return walletDao.getWalletConfirmationDetail(walletId, locale);
    }

    @Override
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId) {
        return walletDao.getWalletShortStatistics(walletId);
    }

    /*
     * Methods defined below are overloaded versions of dashboard info supplier methods.
     * They are supposed to use with REST API which is stateless and cannot use session-based caching.
     * */


    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
        return walletDao.getAllWalletsForUserDetailed(email, currencyIds, withdrawStatusIdForWhichMoneyIsReserved, locale);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email) {
        return walletDao.getAllWalletsForUserReduced(email);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<WalletBalanceDto> getBalancesForUser() {
        String userEmail = userService.getUserEmailFromSecurityContext();
        return walletDao.getBalancesForUser(userEmail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount, String adminEmail) {
        if (amount.equals(BigDecimal.ZERO)) {
            return;
        }
        Wallet wallet = walletDao.findByUserAndCurrency(userId, currencyId);
        if (amount.signum() == -1 && amount.abs().compareTo(wallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException("Negative amount exceeds current balance!");
        }
        if (!isUserAllowedToManuallyChangeWalletBalance(adminEmail, wallet.getUser().getId())) {
            throw new ForbiddenOperationException(String.format("admin: %s, wallet %s", adminEmail, wallet.getId()));
        }
        changeWalletActiveBalance(amount, wallet, OperationType.MANUAL, TransactionSourceType.MANUAL);

    }


    private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                           TransactionSourceType transactionSourceType) {
        changeWalletActiveBalance(amount, wallet, operationType, transactionSourceType, null, null);
    }

    private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                           TransactionSourceType transactionSourceType,
                                           BigDecimal specialCommissionAmount, Integer sourceId) {
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setWalletId(wallet.getId());
        walletOperationData.setAmount(amount);
        walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
        walletOperationData.setOperationType(operationType);
        walletOperationData.setSourceId(sourceId);
        Commission commission = commissionService.findCommissionByTypeAndRole(operationType, userService.getUserRoleFromSecurityContext());
        walletOperationData.setCommission(commission);
        BigDecimal commissionAmount = specialCommissionAmount == null ?
                BigDecimalProcessing.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT) : specialCommissionAmount;
        walletOperationData.setCommissionAmount(commissionAmount);
        walletOperationData.setSourceType(transactionSourceType);
        WalletTransferStatus status = walletBalanceChange(walletOperationData);
        if (status != WalletTransferStatus.SUCCESS) {
            throw new BalanceChangeException(status.name());
        }
        if (commissionAmount.signum() > 0) {

            CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyService.getById(wallet.getCurrencyId()));
            companyWalletService.deposit(companyWallet, BigDecimal.ZERO, commissionAmount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferCostsToUser(Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                                           BigDecimal commissionAmount, Locale locale, int sourceId) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.negativeAmount", null, locale));
        }
        Wallet fromUserWallet = walletDao.findById(fromUserWalletId);
        Integer currencyId = fromUserWallet.getCurrencyId();
        BigDecimal inputAmount = BigDecimalProcessing.doAction(amount, commissionAmount, ActionType.SUBTRACT);
        log.debug(commissionAmount.toString());
        log.debug(inputAmount.toString());
        if (inputAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
        }
        Wallet toUserWallet = walletDao.findByUserAndCurrency(toUserId, currencyId);
        if (toUserWallet == null) {
            throw new WalletNotFoundException(messageSource.getMessage("transfer.walletNotFound", null, locale));
        }
        changeWalletActiveBalance(amount, fromUserWallet, OperationType.OUTPUT,
                TransactionSourceType.USER_TRANSFER, commissionAmount, sourceId);
        changeWalletActiveBalance(inputAmount, toUserWallet, OperationType.INPUT,
                TransactionSourceType.USER_TRANSFER, BigDecimal.ZERO, sourceId);
        String notyAmount = inputAmount.setScale(decimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        return TransferDto.builder()
                .comissionAmount(commissionAmount)
                .notyAmount(notyAmount)
                .walletUserFrom(fromUserWallet)
                .walletUserTo(toUserWallet)
                .initialAmount(amount)
                .currencyId(currencyId)
                .userFromId(fromUserWallet.getUser().getId())
                .userToId(toUserId)
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String transferCostsToUser(Integer userId, Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                                      BigDecimal comission, Locale locale, int sourceId) {
        User toUser = userService.getUserById(toUserId);
        String toUserNickname = toUser.getNickname() != null ? toUser.getNickname() : toUser.getEmail();
        if (toUserId == 0) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.userNotFound", new Object[]{toUserNickname}, locale));
        }
        TransferDto dto = transferCostsToUser(fromUserWalletId, toUserId, amount, comission, locale, sourceId);
        String currencyName = currencyService.getCurrencyName(dto.getCurrencyId());
        String result = messageSource.getMessage("transfer.successful", new Object[]{dto.getNotyAmount(), currencyName, toUserNickname}, locale);
        sendNotificationsAboutTransfer(userId, dto.getNotyAmount(), currencyName, dto.getUserToId(), toUserNickname);
        return result;
    }


    private void sendNotificationsAboutTransfer(int fromUserId, String notyAmount, String currencyName, int toUserId, String toNickName) {
        log.debug("from {} to {}", fromUserId, toUserId);
        notificationService.notifyUser(fromUserId, NotificationEvent.IN_OUT, "wallets.transferTitle",
                "transfer.successful", new Object[]{notyAmount, currencyName, toNickName});
        notificationService.notifyUser(toUserId, NotificationEvent.IN_OUT, "wallets.transferTitle",
                "transfer.received", new Object[]{notyAmount, currencyName});
    }


    @Transactional(rollbackFor = Exception.class)
    public void performTransferCostsToUser(Wallet fromUserWallet, Wallet toUserWallet,
                                           BigDecimal totalAmount,
                                           Locale locale) {
        if (totalAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
        }
        if (Integer.compare(fromUserWallet.getCurrencyId(), toUserWallet.getCurrencyId()) != 0) {
            throw new BalanceChangeException("ncorrect wallets");
        }

    }

    @Override
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public List<UserWalletSummaryDto> getUsersWalletsSummaryForPermittedCurrencyList(Integer requesterUserId, List<Integer> roleIds) {
        List<UserWalletSummaryDto> roleFilteredPaginData = walletDao.getUsersWalletsSummaryNew(requesterUserId, roleIds);

        List<UserWalletSummaryDto> userWalletSummaryDtos = new ArrayList<>();
        for (UserWalletSummaryDto item : roleFilteredPaginData) {
            if (!userWalletSummaryDtos.contains(item)) {
                userWalletSummaryDtos.add(new UserWalletSummaryDto(item));
            } else {
                UserWalletSummaryDto storedItem = userWalletSummaryDtos.stream().filter(e -> e.equals(item)).findAny().get();
                storedItem.increment(item);
            }
        }
        userWalletSummaryDtos.forEach(UserWalletSummaryDto::calculate);

        return userWalletSummaryDtos;
    }

    @Override
    @Transactional
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        return walletDao.getOrderRelatedDataAndBlock(orderId);
    }

    @Override
    @Transactional
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        return walletDao.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
    }

    @Override
    @Transactional
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        return walletDao.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
    }

    @Override
    @Transactional
    public WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId) {
        return walletDao.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(orderId, operationType, currencyPairId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserAllowedToManuallyChangeWalletBalance(String adminEmail, int walletHolderUserId) {
        return walletDao.isUserAllowedToManuallyChangeWalletBalance(userService.getIdByEmail(adminEmail), walletHolderUserId);
    }


    @Override
    public List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups() {
        Supplier<Map<String, BigDecimal>> balancesMapSupplier = () -> Arrays.stream(ReportGroupUserRole.values())
                .collect(toMap(Enum::name, val -> BigDecimal.ZERO));
        return walletDao.getWalletBalancesSummaryByGroups().stream()
                .collect(Collectors.groupingBy(UserGroupBalanceDto::getCurAndId)).entrySet().stream()

                .map(entry -> new UserRoleTotalBalancesReportDto<>(entry.getKey().getCurrency(), entry.getKey().getId(), entry.getValue().stream()
                        .collect(toMap(dto -> dto.getReportGroupUserRole().name(),
                                UserGroupBalanceDto::getTotalBalance, (oldValue, newValue) -> newValue,
                                balancesMapSupplier)), ReportGroupUserRole.class))
                .sorted(comparing(dto -> dto.getCurId()))
                .collect(Collectors.toList());

    }


    @Override
    public List<UserRoleTotalBalancesReportDto<UserRole>> getWalletBalancesSummaryByRoles(List<UserRole> roles) {
        return walletDao.getWalletBalancesSummaryByRoles(roles.stream().map(UserRole::getRole).collect(Collectors.toList()))
                .stream()
                //wolper 19.04.18
                .collect(Collectors.groupingBy(UserRoleBalanceDto::getCurAndId)).entrySet().stream()
                .map(entry -> new UserRoleTotalBalancesReportDto<>(entry.getKey().getCurrency(), entry.getKey().getId(), entry.getValue().stream()
                        .collect(Collectors.toMap(dto -> dto.getUserRole().name(), UserRoleBalanceDto::getTotalBalance)), UserRole.class))
                .sorted(comparing(dto -> dto.getCurId()))
                .collect(Collectors.toList());
    }

    @Override
    public int getWalletIdAndBlock(Integer userId, Integer currencyId) {
        return walletDao.getWalletIdAndBlock(userId, currencyId);
    }

    @Transactional
    @Override
    public void updateExternalMainWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external main wallets start...");

        List<Currency> currencies = currencyService.getAllCurrencies();

        final Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();
        final Map<String, BigDecimal> balances = walletsApi.getBalances();

        if (rates.isEmpty() || balances.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (Currency currency : currencies) {
            final int currencyId = currency.getId();
            final String currencyName = currency.getName();

            Pair<BigDecimal, BigDecimal> pairRates = rates.get(currencyName);
            BigDecimal mainBalance = balances.get(currencyName);

            if (isNull(pairRates) || isNull(mainBalance)) {
                continue;
            }
            final BigDecimal usdRate = pairRates.getLeft();
            final BigDecimal btcRate = pairRates.getRight();

            ExternalWalletBalancesDto exWallet = ExternalWalletBalancesDto.builder()
                    .currencyId(currencyId)
                    .usdRate(usdRate)
                    .btcRate(btcRate)
                    .mainBalance(mainBalance)
                    .build();
            walletDao.updateExternalMainWalletBalances(exWallet);
        }
        log.info("Process of updating external main wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional
    @Override
    public void updateExternalReservedWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external reserved wallets start...");

        final Map<String, BigDecimal> reservedBalances = walletsApi.getReservedBalances();

        if (reservedBalances.isEmpty()) {
            log.info("Wallet api did not return any data");
            return;
        }

        for (Map.Entry<String, BigDecimal> entry: reservedBalances.entrySet()) {
            final String compositeKey = entry.getKey();
            final BigDecimal balance = entry.getValue();

            String[] data = compositeKey.split("\\|\\|");
            final String currencySymbol = data[0];
            final String walletAddress = data[1];

            Currency currency = currencyService.findByName(currencySymbol);

            walletDao.updateExternalReservedWalletBalances(currency.getId(), walletAddress, balance);
        }
        log.info("Process of updating external reserved wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        return walletDao.getExternalMainWalletBalances();
    }

    @Transactional
    @Override
    public void updateInternalWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating internal wallets start...");

        List<Currency> currencies = currencyService.getAllCurrencies();

        final Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();
        final Map<String, List<InternalWalletBalancesDto>> balances = this.getWalletBalances().stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        if (rates.isEmpty() || balances.isEmpty()) {
            log.info("Exchange or wallet api did not return data");
            return;
        }

        for (Currency currency : currencies) {
            final String currencyName = currency.getName();

            Pair<BigDecimal, BigDecimal> pairRates = rates.get(currencyName);
            List<InternalWalletBalancesDto> balancesByRoles = balances.get(currencyName);

            if (isNull(pairRates) || isNull(balancesByRoles)) {
                continue;
            }
            final BigDecimal usdRate = pairRates.getLeft();
            final BigDecimal btcRate = pairRates.getRight();

            for (InternalWalletBalancesDto balance : balancesByRoles) {
                balance = balance.toBuilder()
                        .usdRate(usdRate)
                        .btcRate(btcRate)
                        .build();
                walletDao.updateInternalWalletBalances(balance);
            }
        }
        log.info("Process of updating internal wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        return walletDao.getInternalWalletBalances();
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        return walletDao.getWalletBalances();
    }

    @Override
    public void createWalletAddress(int currencyId) {
        walletDao.createReservedWalletAddress(currencyId);
    }

    @Override
    public void deleteWalletAddress(int id, int currencyId, String walletAddress) {
        walletDao.deleteReservedWalletAddress(id, currencyId);

        final String currencySymbol = currencyService.getCurrencyName(currencyId);

        boolean deleted = walletsApi.deleteReservedWallet(currencySymbol, walletAddress);

        log.info("External reserved address [{}:{}] {}",
                currencySymbol,
                walletAddress,
                deleted ? "have been deleted" : "have not been deleted");
    }

    @Override
    public void updateWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto, boolean isSavedAsAddress) {
        walletDao.updateReservedWalletAddress(externalReservedWalletAddressDto);

        if (isSavedAsAddress) {
            final int currencyId = externalReservedWalletAddressDto.getCurrencyId();
            final String walletAddress = externalReservedWalletAddressDto.getWalletAddress();

            final String currencySymbol = currencyService.getCurrencyName(currencyId);

            boolean saved = walletsApi.addReservedWallet(currencySymbol, walletAddress);

            log.info("External reserved address [{}:{}] {}",
                    currencySymbol,
                    walletAddress,
                    saved ? "have been saved" : "have not been saved");
        }
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId) {
        return walletDao.getReservedWalletsByCurrencyId(currencyId);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public BigDecimal retrieveSummaryUSD() {
        return walletDao.retrieveSummaryUSD();
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public BigDecimal retrieveSummaryBTC() {
        return walletDao.retrieveSummaryBTC();
    }

    @Override
    public BigDecimal getExternalReservedWalletBalance(Integer currencyId, String walletAddress) {
        Currency currency = currencyService.findById(currencyId);
        if (isNull(currency)) {
            log.info("Currency with id: {} not found", currencyId);
            return null;
        }
        return walletsApi.getBalanceByCurrencyAndWallet(currency.getName(), walletAddress);
    }
}
