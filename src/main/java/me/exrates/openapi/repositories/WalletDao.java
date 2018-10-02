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
import me.exrates.openapi.models.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.WalletTransferStatus;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.mappers.CompanyWalletRowMapper;
import me.exrates.openapi.repositories.mappers.WalletBalanceRowMapper;
import me.exrates.openapi.repositories.mappers.WalletRowMapper;
import me.exrates.openapi.repositories.mappers.WalletsForOrderAcceptionRowMapper;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static me.exrates.openapi.models.enums.OperationType.SELL;

@Slf4j
@Repository
public class WalletDao {

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

    private static final String CREATE_NEW_WALLET_SQL = "INSERT INTO WALLET (currency_id, user_id, active_balance) VALUES(:currId, :userId, :activeBalance)";

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

    private final TransactionDao transactionDao;
    private final CurrencyDao currencyDao;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletDao(TransactionDao transactionDao,
                     CurrencyDao currencyDao,
                     NamedParameterJdbcTemplate jdbcTemplate) {
        this.transactionDao = transactionDao;
        this.currencyDao = currencyDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<WalletsForOrderCancelDto> getWalletsForOrderCancelDtoMapper(OperationType operationType) {
        return (rs, i) -> {
            WalletsForOrderCancelDto result = new WalletsForOrderCancelDto();
            result.setOrderId(rs.getInt("order_id"));
            result.setOrderStatusId(rs.getInt("order_status_id"));
            BigDecimal reservedAmount = operationType == SELL ? rs.getBigDecimal("amount_base") :
                    BigDecimalProcessingUtil.doAction(rs.getBigDecimal("amount_convert"), rs.getBigDecimal("commission_fixed_amount"),
                            ActionType.ADD);

            result.setReservedAmount(reservedAmount);
            result.setWalletId(rs.getInt("wallet_id"));
            result.setActiveBalance(rs.getBigDecimal("active_balance"));
            result.setActiveBalance(rs.getBigDecimal("reserved_balance"));
            return result;
        };
    }

    //+
    public BigDecimal getWalletABalance(int walletId) {
        if (walletId == 0) {
            return new BigDecimal(0);
        }
        String sql = "SELECT active_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        CurrencyPair currencyPair = currencyDao.findCurrencyPairByOrderId(orderId);
        String sql = "SELECT " +
                " EXORDERS.id AS order_id, " +
                " EXORDERS.status_id AS order_status_id, " +
                " EXORDERS.amount_base AS amount_base, " +
                " EXORDERS.amount_convert AS amount_convert, " +
                " EXORDERS.commission_fixed_amount AS commission_fixed_amount, " +
                " WALLET.id AS wallet_id, " +
                " WALLET.active_balance AS active_balance, " +
                " WALLET.reserved_balance AS reserved_balance " +
                " FROM EXORDERS  " +
                " JOIN WALLET ON  (WALLET.user_id = EXORDERS.user_id) AND " +
                "             (WALLET.currency_id = :currency_id) " +
                " WHERE (EXORDERS.id = :order_id)" +
                " FOR UPDATE ";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        namedParameters.put("currency_id", operationType == SELL ? currencyPair.getCurrency1().getId() : currencyPair.getCurrency2().getId());
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, getWalletsForOrderCancelDtoMapper(operationType));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        CurrencyPair currencyPair = currencyDao.findCurrencyPairByOrderId(orderId);
        String sql =
                "  SELECT  " +
                        "    EXORDERS.id AS order_id, " +
                        "    EXORDERS.status_id AS order_status_id, " +
                        "    EXORDERS.operation_type_id, EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount," +
                        "    ORDER_CREATOR_RESERVED_WALLET.id AS order_creator_reserved_wallet_id,  " +
                        "    TRANSACTION.id AS transaction_id,  " +
                        "    TRANSACTION.operation_type_id as transaction_type_id,  " +
                        "    TRANSACTION.amount as transaction_amount, " +
                        "    USER_WALLET.id as user_wallet_id,  " +
                        "    COMPANY_WALLET.id as company_wallet_id, " +
                        "    TRANSACTION.commission_amount AS company_commission " +
                        "  FROM EXORDERS " +
                        "    JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON  " +
                        "            (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND  " +
                        "            ( " +
                        "                (EXORDERS.operation_type_id=4 AND ORDER_CREATOR_RESERVED_WALLET.currency_id = :currency2_id)  " +
                        "                OR  " +
                        "                (EXORDERS.operation_type_id=3 AND ORDER_CREATOR_RESERVED_WALLET.currency_id = :currency1_id) " +
                        "            ) " +
                        "    LEFT JOIN TRANSACTION ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id) " +
                        "    LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id) " +
                        "    LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0) " +
                        "  WHERE EXORDERS.id=:deleted_order_id AND EXORDERS.status_id IN (2, 3)" +
                        "  FOR UPDATE ";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("deleted_order_id", orderId);
            put("currency1_id", currencyPair.getCurrency1().getId());
            put("currency2_id", currencyPair.getCurrency2().getId());
        }};
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            Integer operationTypeId = rs.getInt("operation_type_id");
            BigDecimal orderCreatorReservedAmount = operationTypeId == 3 ? rs.getBigDecimal("amount_base") :
                    BigDecimalProcessingUtil.doAction(rs.getBigDecimal("amount_convert"), rs.getBigDecimal("commission_fixed_amount"),
                            ActionType.ADD);
            return new OrderDetailDto(
                    rs.getInt("order_id"),
                    rs.getInt("order_status_id"),
                    orderCreatorReservedAmount,
                    rs.getInt("order_creator_reserved_wallet_id"),
                    rs.getInt("transaction_id"),
                    rs.getInt("transaction_type_id"),
                    rs.getBigDecimal("transaction_amount"),
                    rs.getInt("user_wallet_id"),
                    rs.getInt("company_wallet_id"),
                    rs.getBigDecimal("company_commission")
            );
        });
    }

    //+
    public List<WalletBalanceDto> getUserBalances(String userEmail) {
        return jdbcTemplate.query(GET_USER_BALANCES_SQL, Collections.singletonMap("email", userEmail), WalletBalanceRowMapper.map());
    }

    //+
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        CurrencyPair currencyPair = currencyDao.findCurrencyPairByOrderId(orderId);

        String acceptorId = isNull(userAcceptorId) ? "o.user_acceptor_id" : ":user_acceptor_id";

        return jdbcTemplate.queryForObject(String.format(
                GET_WALLETS_FOR_ORDER_BY_ORDER_ID_AND_BLOCK_SQL, acceptorId, acceptorId),
                Map.of(
                        "order_id", orderId,
                        "currency1_id", currencyPair.getCurrency1().getId(),
                        "currency2_id", currencyPair.getCurrency2().getId(),
                        "user_acceptor_id", userAcceptorId),
                WalletsForOrderAcceptionRowMapper.map(currencyPair));
    }

    //+
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

    //+
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

        BigDecimal newActiveBalance = BigDecimalProcessingUtil.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessingUtil.doAction(wallet.getReservedBalance(), amount, ActionType.SUBTRACT);

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
                .userWallet(wallet)
                .companyWallet(null)
                .amount(amount)
                .commissionAmount(BigDecimal.ZERO)
                .commission(null)
                .currency(Currency.builder()
                        .id(wallet.getCurrencyId())
                        .build())
                .provided(true)
                .activeBalanceBefore(wallet.getActiveBalance())
                .reservedBalanceBefore(wallet.getReservedBalance())
                .companyBalanceBefore(null)
                .companyCommissionBalanceBefore(null)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description(description)
                .build();
        try {
            transactionDao.create(transaction);
        } catch (Exception ex) {
            log.error("Something happened wrong", ex);
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        return WalletTransferStatus.SUCCESS;
    }

    //+
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

        BigDecimal newActiveBalance;
        BigDecimal newReservedBalance;
        if (walletOperationData.getBalanceType() == WalletOperationData.BalanceType.ACTIVE) {
            newActiveBalance = BigDecimalProcessingUtil.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = wallet.getReservedBalance();
        } else {
            newActiveBalance = wallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessingUtil.doAction(wallet.getReservedBalance(), amount, ActionType.ADD);
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
                    .userWallet(wallet)
                    .companyWallet(companyWallet)
                    .amount(walletOperationData.getAmount())
                    .commissionAmount(walletOperationData.getCommissionAmount())
                    .commission(walletOperationData.getCommission())
                    .currency(companyWallet.getCurrency())
                    .provided(true)
                    .activeBalanceBefore(wallet.getActiveBalance())
                    .reservedBalanceBefore(wallet.getReservedBalance())
                    .companyBalanceBefore(companyWallet.getBalance())
                    .companyCommissionBalanceBefore(companyWallet.getCommissionBalance())
                    .sourceType(walletOperationData.getSourceType())
                    .sourceId(walletOperationData.getSourceId())
                    .description(walletOperationData.getDescription())
                    .build();
            try {
                transactionDao.create(transaction);
            } catch (Exception ex) {
                log.error("Something happened wrong", ex);
                return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        } else {
            Transaction transaction = walletOperationData.getTransaction().toBuilder()
                    .provided(true)
                    .userWallet(wallet)
                    .companyWallet(companyWallet)
                    .activeBalanceBefore(wallet.getActiveBalance())
                    .reservedBalanceBefore(wallet.getReservedBalance())
                    .companyBalanceBefore(companyWallet.getBalance())
                    .companyCommissionBalanceBefore(companyWallet.getCommissionBalance())
                    .sourceType(walletOperationData.getSourceType())
                    .sourceId(walletOperationData.getSourceId())
                    .build();
            try {
                transactionDao.updateForProvided(transaction);
            } catch (Exception ex) {
                log.error("Something happened wrong", ex);
                return WalletTransferStatus.TRANSACTION_UPDATE_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        }
        return WalletTransferStatus.SUCCESS;
    }

    //+
    public int getWalletId(int userId, int currencyId) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_WALLET_ID_SQL,
                    Map.of(
                            "userId", userId,
                            "currencyId", currencyId),
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}