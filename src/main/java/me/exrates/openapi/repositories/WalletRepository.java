package me.exrates.openapi.repositories;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.dto.OrderDetailDto;
import me.exrates.openapi.models.dto.WalletsForOrderAcceptionDto;
import me.exrates.openapi.models.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.models.dto.WalletBalanceDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.WalletTransferStatus;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.mappers.CompanyWalletRowMapper;
import me.exrates.openapi.repositories.mappers.OrderDetailRowMapper;
import me.exrates.openapi.repositories.mappers.WalletBalanceRowMapper;
import me.exrates.openapi.repositories.mappers.WalletRowMapper;
import me.exrates.openapi.repositories.mappers.WalletsForOrderAcceptionRowMapper;
import me.exrates.openapi.repositories.mappers.WalletsForOrderCancelRowMapper;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.openapi.models.enums.OperationType.SELL;

@Slf4j
@Repository
public class WalletRepository {

    private static final String GET_USER_BALANCES_SQL = "SELECT c.name AS currency_name, w.active_balance, w.reserved_balance" +
            " FROM WALLET w" +
            " JOIN CURRENCY c ON w.currency_id = c.id " +
            " WHERE w.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

    private static final String GET_WALLETS_FOR_ORDER_BY_ORDER_ID_AND_BLOCK_SQL = "SELECT o.id AS order_id, o.status_id AS order_status_id, " +
            "cw1.id AS company_wallet_currency_base, cw1.balance AS company_wallet_currency_base_balance, " +
            "cw1.commission_balance AS company_wallet_currency_base_commission_balance, cw2.id AS company_wallet_currency_convert, " +
            "cw2.balance AS company_wallet_currency_convert_balance, cw2.commission_balance AS company_wallet_currency_convert_commission_balance," +
            " IF (o.operation_type_id = 4, w1.id, w2.id) AS wallet_in_for_creator, " +
            " IF (o.operation_type_id = 4, w1.active_balance, w2.active_balance) AS wallet_in_active_for_creator, " +
            " IF (o.operation_type_id = 4, w1.reserved_balance, w2.reserved_balance) AS wallet_in_reserved_for_creator, " +
            " IF (o.operation_type_id = 4, w2.id, w1.id) AS wallet_out_for_creator, " +
            " IF (o.operation_type_id = 4, w2.active_balance, w1.active_balance) AS wallet_out_active_for_creator, " +
            " IF (o.operation_type_id = 4, w2.reserved_balance, w1.reserved_balance) AS wallet_out_reserved_for_creator, " +
            " IF (o.operation_type_id = 3, w1a.id, w2a.id) AS wallet_in_for_acceptor, " +
            " IF (o.operation_type_id = 3, w1a.active_balance, w2a.active_balance) AS wallet_in_active_for_acceptor, " +
            " IF (o.operation_type_id = 3, w1a.reserved_balance, w2a.reserved_balance) AS wallet_in_reserved_for_acceptor, " +
            " IF (o.operation_type_id = 3, w2a.id, w1a.id) AS wallet_out_for_acceptor, " +
            " IF (o.operation_type_id = 3, w2a.active_balance, w1a.active_balance) AS wallet_out_active_for_acceptor, " +
            " IF (o.operation_type_id = 3, w2a.reserved_balance, w1a.reserved_balance) AS wallet_out_reserved_for_acceptor" +
            " FROM EXORDERS" +
            " LEFT JOIN COMPANY_WALLET cw1 ON (cw1.currency_id= :currency1_id)" +
            " LEFT JOIN COMPANY_WALLET cw2 ON (cw2.currency_id= :currency2_id)" +
            " LEFT JOIN WALLET w1 ON w1.user_id = o.user_id AND w1.currency_id= :currency1_id" +
            " LEFT JOIN WALLET w2 ON w2.user_id = o.user_id AND w2.currency_id= :currency2_id" +
            " LEFT JOIN WALLET w1a ON w1a.user_id = %s AND w1a.currency_id= :currency1_id" +
            " LEFT JOIN WALLET w2a ON w2a.user_id = %s AND w2a.currency_id= :currency2_id" +
            " WHERE o.id = :order_id" +
            " FOR UPDATE";

    private static final String CREATE_NEW_WALLET_SQL = "INSERT IGNORE INTO WALLET (currency_id, user_id, active_balance) VALUES(:currId, :userId, :activeBalance)";

    private static final String GET_WALLET_BY_ID_SQL = "SELECT w.id AS wallet_id, w.currency_id, w.active_balance, w.reserved_balance" +
            " FROM WALLET w" +
            " WHERE w.id = :walletId" +
            " FOR UPDATE";

    private static final String UPDATE_WALLET_BALANCES_SQL = "UPDATE WALLET w" +
            " SET w.active_balance = :active_balance, w.reserved_balance = :reserved_balance" +
            " WHERE w.id = :walletId";

    private static final String WALLET_BALANCE_CHANGE_SQL = "SELECT w.id AS wallet_id, w.currency_id, w.active_balance, w.reserved_balance" +
            " FROM WALLET w" +
            " WHERE w.id = :walletId" +
            " FOR UPDATE";

    private static final String COMPANY_WALLET_BALANCE_CHANGE_SQL = "SELECT cw.id AS company_wallet_id, cw.currency_id, cw.balance, cw.commission_balance" +
            " FROM COMPANY_WALLET cw" +
            " JOIN WALLET w ON w.currency_id = cw.currency_id" +
            " WHERE w.id = :walletId" +
            " FOR UPDATE";

    private static final String GET_WALLET_ID_SQL = "SELECT w.id FROM WALLET w WHERE w.user_id = :userId AND w.currency_id = :currencyId";

    private static final String GET_ORDER_RELATED_DATA_AND_BLOCK_SQL = "SELECT o.id AS order_id, o.status_id AS order_status_id, o.operation_type_id, " +
            "o.amount_base, o.amount_convert, o.commission_fixed_amount, ORDER_CREATOR_RESERVED_WALLET.id AS order_creator_reserved_wallet_id, " +
            "t.id AS transaction_id, t.operation_type_id as transaction_type_id, t.amount as transaction_amount, USER_WALLET.id as user_wallet_id, " +
            "COMPANY_WALLET.id as company_wallet_id, t.commission_amount AS company_commission" +
            " FROM EXORDERS o" +
            " JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON ORDER_CREATOR_RESERVED_WALLET.user_id = o.user_id" +
            " AND (o.operation_type_id = 4 AND ORDER_CREATOR_RESERVED_WALLET.currency_id = :currency2_id OR o.operation_type_id = 3 AND ORDER_CREATOR_RESERVED_WALLET.currency_id = :currency1_id)" +
            " LEFT JOIN TRANSACTION t ON t.source_type = 'ORDER' AND t.source_id = o.id" +
            " LEFT JOIN WALLET USER_WALLET ON USER_WALLET.id = t.user_wallet_id" +
            " LEFT JOIN COMPANY_WALLET ON COMPANY_WALLET.id = t.company_wallet_id AND t.commission_amount <> 0" +
            " WHERE o.id = :deleted_order_id AND o.status_id IN (2, 3)" +
            " FOR UPDATE ";

    private static final String GET_WALLET_BALANCE_SQL = "SELECT w.active_balance FROM WALLET w WHERE w.id = :walletId";

    private static final String GET_WALLET_FOR_ORDER_BY_ORDER_ID_AND_OPERATION_TYPE_AND_BLOCK_SQL = "SELECT o.id AS order_id, o.status_id AS order_status_id, " +
            "o.amount_base AS amount_base, o.amount_convert AS amount_convert, o.commission_fixed_amount AS commission_fixed_amount, " +
            "w.id AS wallet_id, w.active_balance AS active_balance, w.reserved_balance AS reserved_balance" +
            " FROM EXORDERS o" +
            " JOIN WALLET w ON w.user_id = o.user_id AND w.currency_id = :currency_id" +
            " WHERE o.id = :order_id" +
            " FOR UPDATE ";

    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletRepository(TransactionRepository transactionRepository,
                            CurrencyRepository currencyRepository,
                            NamedParameterJdbcTemplate jdbcTemplate) {
        this.transactionRepository = transactionRepository;
        this.currencyRepository = currencyRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WalletBalanceDto> getUserBalances(String userEmail) {
        return jdbcTemplate.query(
                GET_USER_BALANCES_SQL,
                Map.of("email", userEmail),
                WalletBalanceRowMapper.map());
    }

    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        CurrencyPair currencyPair = currencyRepository.findCurrencyPairByOrderId(orderId);

        String acceptorId = isNull(userAcceptorId) ? "o.user_acceptor_id" : ":user_acceptor_id";

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto;
        try {
            walletsForOrderAcceptionDto = jdbcTemplate.queryForObject(
                    String.format(GET_WALLETS_FOR_ORDER_BY_ORDER_ID_AND_BLOCK_SQL, acceptorId, acceptorId),
                    Map.of(
                            "order_id", orderId,
                            "currency1_id", currencyPair.getCurrency1().getId(),
                            "currency2_id", currencyPair.getCurrency2().getId(),
                            "user_acceptor_id", userAcceptorId),
                    WalletsForOrderAcceptionRowMapper.map(currencyPair));
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Wallet not found [orderId: %s, userAcceptorId: %s]", orderId, userAcceptorId));
        }
        return walletsForOrderAcceptionDto;
    }

    public int createNewWallet(Wallet wallet) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(
                CREATE_NEW_WALLET_SQL,
                new MapSqlParameterSource(
                        Map.of(
                                "currId", wallet.getCurrencyId(),
                                "userId", wallet.getUser().getId(),
                                "activeBalance", wallet.getActiveBalance())),
                keyHolder);
        return update <= 0 ? 0 : Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public WalletTransferStatus walletInnerTransfer(int walletId,
                                                    BigDecimal amount,
                                                    TransactionSourceType sourceType,
                                                    int sourceId,
                                                    String description) {
        Wallet wallet;
        try {
            wallet = jdbcTemplate.queryForObject(
                    GET_WALLET_BY_ID_SQL,
                    Map.of("walletId", walletId),
                    WalletRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }

        Wallet checkedWallet = Objects.requireNonNull(wallet, "Wallet could not be null");

        BigDecimal newActiveBalance = BigDecimalProcessingUtil.doAction(checkedWallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessingUtil.doAction(checkedWallet.getReservedBalance(), amount, ActionType.SUBTRACT);

        if (newActiveBalance.compareTo(BigDecimal.ZERO) < 0 || newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Negative balance: active {}, reserved {}",
                    BigDecimalProcessingUtil.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessingUtil.formatNonePoint(newReservedBalance, false));
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }

        int update = jdbcTemplate.update(
                UPDATE_WALLET_BALANCES_SQL,
                Map.of(
                        "active_balance", newActiveBalance,
                        "reserved_balance", newReservedBalance,
                        "walletId", walletId));
        if (update <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }

        Transaction transaction = Transaction.builder()
                .operationType(OperationType.WALLET_INNER_TRANSFER)
                .userWallet(checkedWallet)
                .companyWallet(null)
                .amount(amount)
                .commissionAmount(BigDecimal.ZERO)
                .commission(null)
                .currency(Currency.builder()
                        .id(checkedWallet.getCurrencyId())
                        .build())
                .provided(true)
                .activeBalanceBefore(checkedWallet.getActiveBalance())
                .reservedBalanceBefore(checkedWallet.getReservedBalance())
                .companyBalanceBefore(null)
                .companyCommissionBalanceBefore(null)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description(description)
                .build();
        try {
            transactionRepository.create(transaction);
        } catch (Exception ex) {
            log.error("Something happened wrong", ex);
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        return WalletTransferStatus.SUCCESS;
    }

    public WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData) {
        BigDecimal amount = walletOperationData.getAmount();
        if (walletOperationData.getOperationType() == OperationType.OUTPUT) {
            amount = amount.negate();
        }

        Wallet wallet;
        CompanyWallet companyWallet;
        try {
            wallet = jdbcTemplate.queryForObject(
                    WALLET_BALANCE_CHANGE_SQL,
                    Map.of("walletId", walletOperationData.getWalletId()),
                    WalletRowMapper.map());
            companyWallet = jdbcTemplate.queryForObject(
                    COMPANY_WALLET_BALANCE_CHANGE_SQL,
                    Map.of("walletId", walletOperationData.getWalletId()),
                    CompanyWalletRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            log.error("Wallet not found", ex);
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }

        Wallet checkedWallet = Objects.requireNonNull(wallet, "Wallet could not be null");
        CompanyWallet checkedCompanyWallet = Objects.requireNonNull(companyWallet, "Company wallet could not be null");

        BigDecimal newActiveBalance;
        BigDecimal newReservedBalance;
        if (walletOperationData.getBalanceType() == WalletOperationData.BalanceType.ACTIVE) {
            newActiveBalance = BigDecimalProcessingUtil.doAction(checkedWallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = checkedWallet.getReservedBalance();
        } else {
            newActiveBalance = checkedWallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessingUtil.doAction(checkedWallet.getReservedBalance(), amount, ActionType.ADD);
        }
        if (newActiveBalance.compareTo(BigDecimal.ZERO) < 0 || newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Negative balance: active {}, reserved {}",
                    BigDecimalProcessingUtil.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessingUtil.formatNonePoint(newReservedBalance, false));
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }

        int update = jdbcTemplate.update(
                UPDATE_WALLET_BALANCES_SQL,
                Map.of(
                        "active_balance", newActiveBalance,
                        "reserved_balance", newReservedBalance,
                        "walletId", walletOperationData.getWalletId()));
        if (update <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }

        if (isNull(walletOperationData.getTransaction())) {
            Transaction transaction = Transaction.builder()
                    .operationType(walletOperationData.getOperationType())
                    .userWallet(checkedWallet)
                    .companyWallet(checkedCompanyWallet)
                    .amount(walletOperationData.getAmount())
                    .commissionAmount(walletOperationData.getCommissionAmount())
                    .commission(walletOperationData.getCommission())
                    .currency(checkedCompanyWallet.getCurrency())
                    .provided(true)
                    .activeBalanceBefore(checkedWallet.getActiveBalance())
                    .reservedBalanceBefore(checkedWallet.getReservedBalance())
                    .companyBalanceBefore(checkedCompanyWallet.getBalance())
                    .companyCommissionBalanceBefore(checkedCompanyWallet.getCommissionBalance())
                    .sourceType(walletOperationData.getSourceType())
                    .sourceId(walletOperationData.getSourceId())
                    .description(walletOperationData.getDescription())
                    .build();
            try {
                transactionRepository.create(transaction);
            } catch (Exception ex) {
                log.error("Something happened wrong", ex);
                return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        } else {
            Transaction transaction = walletOperationData.getTransaction().toBuilder()
                    .provided(true)
                    .userWallet(checkedWallet)
                    .companyWallet(checkedCompanyWallet)
                    .activeBalanceBefore(checkedWallet.getActiveBalance())
                    .reservedBalanceBefore(checkedWallet.getReservedBalance())
                    .companyBalanceBefore(checkedCompanyWallet.getBalance())
                    .companyCommissionBalanceBefore(checkedCompanyWallet.getCommissionBalance())
                    .sourceType(walletOperationData.getSourceType())
                    .sourceId(walletOperationData.getSourceId())
                    .build();
            try {
                transactionRepository.updateForProvided(transaction);
            } catch (Exception ex) {
                log.error("Something happened wrong", ex);
                return WalletTransferStatus.TRANSACTION_UPDATE_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        }
        return WalletTransferStatus.SUCCESS;
    }

    public int getWalletId(int userId, int currencyId) {
        try {
            Integer walletId = jdbcTemplate.queryForObject(
                    GET_WALLET_ID_SQL,
                    Map.of(
                            "userId", userId,
                            "currencyId", currencyId),
                    Integer.class);
            return nonNull(walletId) ? walletId : 0;
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
    }

    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        CurrencyPair currencyPair = currencyRepository.findCurrencyPairByOrderId(orderId);

        return jdbcTemplate.query(
                GET_ORDER_RELATED_DATA_AND_BLOCK_SQL,
                Map.of(
                        "deleted_order_id", orderId,
                        "currency1_id", currencyPair.getCurrency1().getId(),
                        "currency2_id", currencyPair.getCurrency2().getId()),
                OrderDetailRowMapper.map());
    }

    public BigDecimal getWalletABalance(int walletId) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_WALLET_BALANCE_SQL,
                    Map.of("walletId", walletId == 0 ? BigDecimal.ZERO : walletId),
                    BigDecimal.class);
        } catch (EmptyResultDataAccessException ex) {
            return BigDecimal.ZERO;
        }
    }

    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        CurrencyPair currencyPair = currencyRepository.findCurrencyPairByOrderId(orderId);

        try {
            return jdbcTemplate.queryForObject(
                    GET_WALLET_FOR_ORDER_BY_ORDER_ID_AND_OPERATION_TYPE_AND_BLOCK_SQL,
                    Map.of(
                            "order_id", orderId,
                            "currency_id", operationType == SELL
                                    ? currencyPair.getCurrency1().getId()
                                    : currencyPair.getCurrency2().getId()),
                    WalletsForOrderCancelRowMapper.map(operationType));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}