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
import me.exrates.openapi.repositories.mappers.WalletBalanceRowMapper;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
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

import static me.exrates.openapi.models.enums.OperationType.SELL;

@Slf4j
@Repository
public class WalletDao {

    private static final String GET_USER_BALANCES_SQL = "SELECT c.name AS currency_name, w.active_balance, w.reserved_balance" +
            " FROM WALLET w" +
            " JOIN CURRENCY c ON w.currency_id = c.id " +
            " WHERE w.user_id = (SELECT u.id FROM USER u WHERE u.email = :email)";

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
    public int getWalletId(int userId, int currencyId) {
        String sql = "SELECT id FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("userId", String.valueOf(userId));
        namedParameters.put("currencyId", String.valueOf(currencyId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    //+
    public int createNewWallet(Wallet wallet) {
        String sql = "INSERT INTO WALLET (currency_id,user_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("currId", wallet.getCurrencyId())
                .addValue("userId", wallet.getUser().getId())
                .addValue("activeBalance", wallet.getActiveBalance());
        int result = jdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    //+
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        CurrencyPair currencyPair = currencyDao.findCurrencyPairByOrderId(orderId);
        String sql = "SELECT " +
                " EXORDERS.id AS order_id, " +
                " EXORDERS.status_id AS order_status_id, " +
                " cw1.id AS company_wallet_currency_base, " +
                " cw1.balance AS company_wallet_currency_base_balance, " +
                " cw1.commission_balance AS company_wallet_currency_base_commission_balance, " +
                " cw2.id AS company_wallet_currency_convert, " +
                " cw2.balance AS company_wallet_currency_convert_balance, " +
                " cw2.commission_balance AS company_wallet_currency_convert_commission_balance, " +

                " IF (EXORDERS.operation_type_id=4, w1.id, w2.id) AS wallet_in_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w1.active_balance, w2.active_balance) AS wallet_in_active_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w1.reserved_balance, w2.reserved_balance) AS wallet_in_reserved_for_creator, " +

                " IF (EXORDERS.operation_type_id=4, w2.id, w1.id) AS wallet_out_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.active_balance, w1.active_balance) AS wallet_out_active_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.reserved_balance, w1.reserved_balance) AS wallet_out_reserved_for_creator, " +

                " IF (EXORDERS.operation_type_id=3, w1a.id, w2a.id) AS wallet_in_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w1a.active_balance, w2a.active_balance) AS wallet_in_active_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w1a.reserved_balance, w2a.reserved_balance) AS wallet_in_reserved_for_acceptor, " +

                " IF (EXORDERS.operation_type_id=3, w2a.id, w1a.id) AS wallet_out_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.active_balance, w1a.active_balance) AS wallet_out_active_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.reserved_balance, w1a.reserved_balance) AS wallet_out_reserved_for_acceptor" +
                " FROM EXORDERS  " +
                " LEFT JOIN COMPANY_WALLET cw1 ON (cw1.currency_id= :currency1_id) " +
                " LEFT JOIN COMPANY_WALLET cw2 ON (cw2.currency_id= :currency2_id) " +
                " LEFT JOIN WALLET w1 ON  (w1.user_id = EXORDERS.user_id) AND " +
                "             (w1.currency_id= :currency1_id) " +
                " LEFT JOIN WALLET w2 ON  (w2.user_id = EXORDERS.user_id) AND " +
                "             (w2.currency_id= :currency2_id) " +
                " LEFT JOIN WALLET w1a ON  (w1a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w1a.currency_id= :currency1_id)" +
                " LEFT JOIN WALLET w2a ON  (w2a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w2a.currency_id= :currency2_id) " +
                " WHERE (EXORDERS.id = :order_id)" +
                " FOR UPDATE ";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        namedParameters.put("currency1_id", currencyPair.getCurrency1().getId());
        namedParameters.put("currency2_id", currencyPair.getCurrency2().getId());
        if (userAcceptorId != null) {
            namedParameters.put("user_acceptor_id", String.valueOf(userAcceptorId));
        }
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, (rs, i) -> {
                WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
                walletsForOrderAcceptionDto.setOrderId(rs.getInt("order_id"));
                walletsForOrderAcceptionDto.setOrderStatusId(rs.getInt("order_status_id"));
                /**/
                walletsForOrderAcceptionDto.setCurrencyBase(currencyPair.getCurrency1().getId());
                walletsForOrderAcceptionDto.setCurrencyConvert(currencyPair.getCurrency2().getId());
                /**/
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBase(rs.getInt("company_wallet_currency_base"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBaseBalance(rs.getBigDecimal("company_wallet_currency_base_balance"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBaseCommissionBalance(rs.getBigDecimal("company_wallet_currency_base_commission_balance"));
                /**/
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvert(rs.getInt("company_wallet_currency_convert"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvertBalance(rs.getBigDecimal("company_wallet_currency_convert_balance"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvertCommissionBalance(rs.getBigDecimal("company_wallet_currency_convert_commission_balance"));
                /**/
                walletsForOrderAcceptionDto.setUserCreatorInWalletId(rs.getInt("wallet_in_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_creator"));
                /**/
                walletsForOrderAcceptionDto.setUserCreatorOutWalletId(rs.getInt("wallet_out_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_creator"));
                /**/
                walletsForOrderAcceptionDto.setUserAcceptorInWalletId(rs.getInt("wallet_in_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_acceptor"));
                /**/
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletId(rs.getInt("wallet_out_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_acceptor"));
                /**/
                return walletsForOrderAcceptionDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
        String sql = "SELECT WALLET.id AS wallet_id, WALLET.currency_id, WALLET.active_balance, WALLET.reserved_balance" +
                "  FROM WALLET " +
                "  WHERE WALLET.id = :walletId " +
                "  FOR UPDATE";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        Wallet wallet;
        try {
            wallet = jdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                Wallet result = new Wallet();
                result.setId(rs.getInt("wallet_id"));
                result.setCurrencyId(rs.getInt("currency_id"));
                result.setActiveBalance(rs.getBigDecimal("active_balance"));
                result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                return result;
            });
        } catch (EmptyResultDataAccessException e) {
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }
        /**/
        BigDecimal newActiveBalance = BigDecimalProcessingUtil.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessingUtil.doAction(wallet.getReservedBalance(), amount, ActionType.SUBTRACT);
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            log.error(String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessingUtil.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessingUtil.formatNonePoint(newReservedBalance, false)));
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }
        /**/
        sql = "UPDATE WALLET SET active_balance = :active_balance, reserved_balance = :reserved_balance WHERE id =:walletId";
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("active_balance", newActiveBalance);
                put("reserved_balance", newReservedBalance);
                put("walletId", String.valueOf(walletId));
            }
        };
        if (jdbcTemplate.update(sql, params) <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }
        /**/
        Transaction transaction = new Transaction();
        transaction.setOperationType(OperationType.WALLET_INNER_TRANSFER);
        transaction.setUserWallet(wallet);
        transaction.setCompanyWallet(null);
        transaction.setAmount(amount);
        transaction.setCommissionAmount(BigDecimal.ZERO);
        transaction.setCommission(null);
        Currency currency = new Currency();
        currency.setId(wallet.getCurrencyId());
        transaction.setCurrency(currency);
        transaction.setProvided(true);
        transaction.setActiveBalanceBefore(wallet.getActiveBalance());
        transaction.setReservedBalanceBefore(wallet.getReservedBalance());
        transaction.setCompanyBalanceBefore(null);
        transaction.setCompanyCommissionBalanceBefore(null);
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        transaction.setDescription(description);
        try {
            transactionDao.create(transaction);
        } catch (Exception e) {
            log.error("Something happened wrong", e);
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        /**/
        return WalletTransferStatus.SUCCESS;
    }

    //+
    public WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData) {
        BigDecimal amount = walletOperationData.getAmount();
        if (walletOperationData.getOperationType() == OperationType.OUTPUT) {
            amount = amount.negate();
        }
        /**/
        CompanyWallet companyWallet = new CompanyWallet();
        String sql = "SELECT WALLET.id AS wallet_id, WALLET.currency_id, WALLET.active_balance, WALLET.reserved_balance, " +
                "  COMPANY_WALLET.id AS company_wallet_id, COMPANY_WALLET.currency_id, COMPANY_WALLET.balance, COMPANY_WALLET.commission_balance " +
                "  FROM WALLET " +
                "  JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = WALLET.currency_id) " +
                "  WHERE WALLET.id = :walletId " +
                "  FOR UPDATE";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletOperationData.getWalletId()));
        Wallet wallet;
        try {
            wallet = jdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                Wallet result = new Wallet();
                result.setId(rs.getInt("wallet_id"));
                result.setCurrencyId(rs.getInt("currency_id"));
                result.setActiveBalance(rs.getBigDecimal("active_balance"));
                result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                /**/
                companyWallet.setId(rs.getInt("company_wallet_id"));
                Currency currency = new Currency();
                currency.setId(rs.getInt("currency_id"));
                companyWallet.setCurrency(currency);
                companyWallet.setBalance(rs.getBigDecimal("balance"));
                companyWallet.setCommissionBalance(rs.getBigDecimal("commission_balance"));
                return result;
            });
        } catch (EmptyResultDataAccessException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }
        /**/
        BigDecimal newActiveBalance;
        BigDecimal newReservedBalance;
        if (walletOperationData.getBalanceType() == WalletOperationData.BalanceType.ACTIVE) {
            newActiveBalance = BigDecimalProcessingUtil.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = wallet.getReservedBalance();
        } else {
            newActiveBalance = wallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessingUtil.doAction(wallet.getReservedBalance(), amount, ActionType.ADD);
        }
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            log.error(String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessingUtil.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessingUtil.formatNonePoint(newReservedBalance, false)));
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }
        /**/
        sql = "UPDATE WALLET SET active_balance = :active_balance, reserved_balance = :reserved_balance WHERE id =:walletId";
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("active_balance", newActiveBalance);
                put("reserved_balance", newReservedBalance);
                put("walletId", String.valueOf(walletOperationData.getWalletId()));
            }
        };
        if (jdbcTemplate.update(sql, params) <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }
        /**/
        if (walletOperationData.getTransaction() == null) {
            Transaction transaction = new Transaction();
            transaction.setOperationType(walletOperationData.getOperationType());
            transaction.setUserWallet(wallet);
            transaction.setCompanyWallet(companyWallet);
            transaction.setAmount(walletOperationData.getAmount());
            transaction.setCommissionAmount(walletOperationData.getCommissionAmount());
            transaction.setCommission(walletOperationData.getCommission());
            transaction.setCurrency(companyWallet.getCurrency());
            transaction.setProvided(true);
            transaction.setActiveBalanceBefore(wallet.getActiveBalance());
            transaction.setReservedBalanceBefore(wallet.getReservedBalance());
            transaction.setCompanyBalanceBefore(companyWallet.getBalance());
            transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
            transaction.setSourceType(walletOperationData.getSourceType());
            transaction.setSourceId(walletOperationData.getSourceId());
            transaction.setDescription(walletOperationData.getDescription());
            try {
                transactionDao.create(transaction);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        } else {
            Transaction transaction = walletOperationData.getTransaction();
            transaction.setProvided(true);
            transaction.setUserWallet(wallet);
            transaction.setCompanyWallet(companyWallet);
            transaction.setActiveBalanceBefore(wallet.getActiveBalance());
            transaction.setReservedBalanceBefore(wallet.getReservedBalance());
            transaction.setCompanyBalanceBefore(companyWallet.getBalance());
            transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
            transaction.setSourceType(walletOperationData.getSourceType());
            transaction.setSourceId(walletOperationData.getSourceId());
            try {
                transactionDao.updateForProvided(transaction);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                return WalletTransferStatus.TRANSACTION_UPDATE_ERROR;
            }
            walletOperationData.setTransaction(transaction);
        }
        /**/
        return WalletTransferStatus.SUCCESS;
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
}