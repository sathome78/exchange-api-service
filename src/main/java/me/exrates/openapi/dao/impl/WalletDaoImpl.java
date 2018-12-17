package me.exrates.openapi.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.CurrencyDao;
import me.exrates.openapi.dao.TransactionDao;
import me.exrates.openapi.dao.UserDao;
import me.exrates.openapi.dao.WalletDao;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.*;
import me.exrates.openapi.model.dto.*;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.openapi.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.openapi.model.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.model.enums.*;
import me.exrates.openapi.model.util.BigDecimalProcessing;
import me.exrates.openapi.model.vo.WalletOperationData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static me.exrates.openapi.model.enums.OperationType.SELL;

@Repository
@Log4j2
public class WalletDaoImpl implements WalletDao {

    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CurrencyDao currencyDao;
    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    protected final RowMapper<Wallet> walletRowMapper = (resultSet, i) -> {

        final Wallet userWallet = new Wallet();
        userWallet.setId(resultSet.getInt("id"));
        userWallet.setName(resultSet.getString("name"));
        userWallet.setCurrencyId(resultSet.getInt("currency_id"));
        userWallet.setUser(userDao.getUserById(resultSet.getInt("user_id")));
        userWallet.setActiveBalance(resultSet.getBigDecimal("active_balance"));
        userWallet.setReservedBalance(resultSet.getBigDecimal("reserved_balance"));

        return userWallet;
    };

    private RowMapper<WalletsForOrderCancelDto> getWalletsForOrderCancelDtoMapper(OperationType operationType) {
        return (rs, i) -> {
            WalletsForOrderCancelDto result = new WalletsForOrderCancelDto();
            result.setOrderId(rs.getInt("order_id"));
            result.setOrderStatusId(rs.getInt("order_status_id"));
            BigDecimal reservedAmount = operationType == SELL ? rs.getBigDecimal("amount_base") :
                    BigDecimalProcessing.doAction(rs.getBigDecimal("amount_convert"), rs.getBigDecimal("commission_fixed_amount"),
                            ActionType.ADD);

            result.setReservedAmount(reservedAmount);
            result.setWalletId(rs.getInt("wallet_id"));
            result.setActiveBalance(rs.getBigDecimal("active_balance"));
            result.setActiveBalance(rs.getBigDecimal("reserved_balance"));
            return result;
        };
    }


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

    public BigDecimal getWalletRBalance(int walletId) {
        String sql = "SELECT reserved_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

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

    @Override
    public int getWalletIdAndBlock(Integer userId, Integer currencyId) {
        String sql = "SELECT id FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId FOR UPDATE";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("userId", String.valueOf(userId));
        namedParameters.put("currencyId", String.valueOf(currencyId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

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

    @Override
    public List<Wallet> findAllByUser(int userId) {
        final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET" +
                "  INNER JOIN CURRENCY On WALLET.currency_id = CURRENCY.id and WALLET.user_id = :userId " +
                " WHERE CURRENCY.hidden != 1 ";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("userId", userId);
            }
        };
        ArrayList<Wallet> result = (ArrayList<Wallet>) slaveJdbcTemplate.query(sql, params,
                walletRowMapper);

        return result;
    }

    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email) {
        String typeClause = "";
        final String sql =
                " SELECT CURRENCY.name, CURRENCY.description, WALLET.active_balance, (WALLET.reserved_balance + WALLET.active_balance) as total_balance " +
                        " FROM USER " +
                        "   JOIN WALLET ON (WALLET.user_id = USER.id) " +
                        "   LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " WHERE USER.email = :email  AND CURRENCY.hidden != 1 " + typeClause +
                        " ORDER BY active_balance DESC, CURRENCY.name ASC ";
        final Map<String, Object> params = new HashMap() {{
            put("email", email);
        }};
        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            MyWalletsStatisticsDto myWalletsStatisticsDto = new MyWalletsStatisticsDto();
            myWalletsStatisticsDto.setCurrencyName(rs.getString("name"));
            myWalletsStatisticsDto.setDescription(rs.getString("description"));
            myWalletsStatisticsDto.setActiveBalance(BigDecimalProcessing.formatNonePoint(rs.getBigDecimal("active_balance"), true));
            myWalletsStatisticsDto.setTotalBalance(BigDecimalProcessing.formatNonePoint(rs.getBigDecimal("total_balance"), true));
            return myWalletsStatisticsDto;
        });
    }

    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserAndCurrenciesReduced(String email, Set<Integer> currencyIds) {
        final String sql =
                " SELECT CURRENCY.name, CURRENCY.description, WALLET.active_balance, (WALLET.reserved_balance + WALLET.active_balance) as total_balance " +
                        " FROM USER " +
                        "   JOIN WALLET ON (WALLET.user_id = USER.id) " +
                        "   LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " WHERE USER.email = :email  AND CURRENCY.hidden != 1 AND  CURRENCY.id IN (:currencies) " +
                        " ORDER BY active_balance DESC, CURRENCY.name ASC ";
        final Map<String, Object> params = new HashMap() {{
            put("email", email);
            put("currencies", currencyIds);
        }};
        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            MyWalletsStatisticsDto myWalletsStatisticsDto = new MyWalletsStatisticsDto();
            myWalletsStatisticsDto.setCurrencyName(rs.getString("name"));
            myWalletsStatisticsDto.setDescription(rs.getString("description"));
            myWalletsStatisticsDto.setActiveBalance(BigDecimalProcessing.formatNonePoint(rs.getBigDecimal("active_balance"), true));
            myWalletsStatisticsDto.setTotalBalance(BigDecimalProcessing.formatNonePoint(rs.getBigDecimal("total_balance"), true));
            return myWalletsStatisticsDto;
        });
    }

    @Override
    public List<WalletBalanceDto> getBalancesForUser(String userEmail) {
        String sql = "SELECT CUR.name AS currency_name, W.active_balance, W.reserved_balance FROM WALLET W " +
                " JOIN CURRENCY CUR ON W.currency_id = CUR.id " +
                " WHERE W.user_id = (SELECT id FROM USER WHERE email = :email)";
        return slaveJdbcTemplate.query(sql, Collections.singletonMap("email", userEmail), (rs, rowNum) -> {
            WalletBalanceDto dto = new WalletBalanceDto();
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setActiveBalance(rs.getBigDecimal("active_balance"));
            dto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
            return dto;
        });
    }

    @Override
    public MyWalletsStatisticsApiDto getWalletShortStatistics(int walletId) {
        final String sql = "SELECT CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, WALLET.id, WALLET.user_id, " +
                "WALLET.active_balance, WALLET.reserved_balance " +
                "FROM WALLET " +
                "INNER JOIN CURRENCY ON CURRENCY.id = WALLET.currency_id " +
                "WHERE WALLET.id = :wallet_id";
        final Map<String, Integer> params = Collections.singletonMap("wallet_id", walletId);
        return slaveJdbcTemplate.queryForObject(sql, params, (resultSet, i) -> {
            MyWalletsStatisticsApiDto dto = new MyWalletsStatisticsApiDto();
            dto.setWalletId(resultSet.getInt("id"));
            dto.setUserId(resultSet.getInt("user_id"));
            dto.setCurrencyId(resultSet.getInt("currency_id"));
            dto.setCurrencyName(resultSet.getString("currency_name"));
            dto.setActiveBalance(resultSet.getBigDecimal("active_balance"));
            dto.setReservedBalance(resultSet.getBigDecimal("reserved_balance"));
            return dto;
        });
    }


    @Override
    public List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId, List<Integer> withdrawEndStatusIds,
                                                                      List<Integer> withdrawSuccessStatusIds,
                                                                      List<Integer> refillSuccessStatusIds) {
        String sql = "SELECT wallet_id, user_id, currency_id, currency_name, active_balance, reserved_balance, " +
                "               SUM(amount_base+amount_convert+commission_fixed_amount) AS reserved_balance_by_orders, " +
                "               SUM(withdraw_amount) AS reserved_balance_by_withdraw, " +
                "               SUM(total_input) AS total_input, SUM(total_output) AS total_output, " +
                "               SUM(total_sell) AS total_sell, SUM(total_buy) AS total_buy " +
                "             FROM " +
                "             ( " +


                "              /*SELL - ORDERS - RESERVE */ " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "             IFNULL(SELL.amount_base,0) AS amount_base, 0 AS amount_convert, 0 AS commission_fixed_amount, " +
                "             0 AS withdraw_amount, 0 AS withdraw_commission, " +
                "             0 AS total_sell, 0 AS total_buy, 0 AS total_input, 0 AS total_output " +
                "             FROM USER " +
                "             JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "             LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "             LEFT JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                "             LEFT JOIN EXORDERS SELL ON (SELL.operation_type_id=3) AND (SELL.user_id=USER.id) AND (SELL.currency_pair_id = CP1.id) AND (SELL.status_id = 2) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*SELL - STOP ORDERS - RESERVE */ " +
                "             UNION ALL " +
                "                 SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, WALLET.active_balance, " +
                "                 WALLET.reserved_balance, " +
                "                 IFNULL(SOSELL.amount_base,0), 0, 0, " +
                "                 0, 0, " +
                "                 0, 0, 0, 0 " +
                "                 FROM USER " +
                "                 JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "                 LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "                 LEFT JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                "                 LEFT JOIN STOP_ORDERS SOSELL ON (SOSELL.operation_type_id=3) AND (SOSELL.user_id=USER.id) AND (SOSELL.currency_pair_id = CP1.id) AND (SOSELL.status_id = 2) " +
                "                 WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*BUY - STOP ORDERS - RESERVE */ " +
                "             UNION ALL " +
                "                 SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, WALLET.active_balance, WALLET.reserved_balance, " +
                "                 0, IFNULL(SOBUY.amount_convert,0), IFNULL(SOBUY.commission_fixed_amount,0), " +
                "                 0, 0, " +
                "                 0, 0, 0, 0 " +
                "                 FROM USER " +
                "                 JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "                 LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "                 LEFT JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                "                 LEFT JOIN STOP_ORDERS SOBUY ON (SOBUY.operation_type_id=4) AND (SOBUY.user_id=USER.id) AND (SOBUY.currency_pair_id = CP2.id) AND (SOBUY.status_id = 2) " +
                "                 WHERE USER.id = :id  AND CURRENCY.hidden != 1 " +


                "             /*BUY - ORDERS - RESERVE */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name,  WALLET.active_balance, WALLET.reserved_balance, " +
                "             0, IFNULL(BUY.amount_convert,0), IFNULL(BUY.commission_fixed_amount,0), " +
                "             0, 0, " +
                "             0, 0, 0, 0 " +
                "             FROM USER " +
                "             JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "             LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "             LEFT JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                "             LEFT JOIN EXORDERS BUY ON (BUY.operation_type_id=4) AND (BUY.user_id=USER.id) AND (BUY.currency_pair_id = CP2.id) AND (BUY.status_id = 2) " +
                "             WHERE USER.id = :id  AND CURRENCY.hidden != 1 " +


                "             /*WITHDRAW - RESERVE */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name,  WALLET.active_balance, WALLET.reserved_balance, " +
                "             0, 0, 0, " +
                "             IFNULL(WITHDRAW_REQUEST.amount, 0), IFNULL(WITHDRAW_REQUEST.commission, 0), " +
                "             0, 0, 0, 0 " +
                "             FROM USER " +
                "             JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "             LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "             JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.user_id = USER.id AND WITHDRAW_REQUEST.currency_id = WALLET.currency_id AND WITHDRAW_REQUEST.status_id NOT IN (:withdraw_status_id_list) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*TRANSFER - RESERVE */ " +
                "             UNION ALL " +
                "                 SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, WALLET.active_balance, WALLET.reserved_balance, " +
                "                 0, 0, 0, " +
                "                 IFNULL(TRANSFER_REQUEST.amount, 0), IFNULL(TRANSFER_REQUEST.commission, 0), " +
                "                 0, 0, 0, 0 " +
                "                 FROM USER " +
                "                 JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "                 LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "                 JOIN TRANSFER_REQUEST ON TRANSFER_REQUEST.user_id = USER.id AND TRANSFER_REQUEST.currency_id = WALLET.currency_id AND TRANSFER_REQUEST.status_id = 4 " +
                "                 WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                " /*CLOSED ORDERS - BASE CURRENCY - CREATOR */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                    WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "                    0, 0, 0, " +
                "                    0, 0, " +
                "                    IF(EO.operation_type_id = 3, EO.amount_base, 0), IF(EO.operation_type_id = 4, EO.amount_base, 0), 0, 0 " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                "               JOIN EXORDERS EO ON (EO.currency_pair_id = CP1.id) AND (EO.status_id = 3) AND (EO.user_id=USER.id) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*CLOSED ORDERS - CONVERT CURRENCY - CREATOR */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                    WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "               0, 0, 0, " +
                "               0, 0, " +
                "               IF(EO.operation_type_id = 4, EO.amount_convert, 0), IF(EO.operation_type_id = 3, EO.amount_convert, 0), 0, 0 " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                "               JOIN EXORDERS EO ON (EO.currency_pair_id = CP2.id) AND (EO.status_id = 3) AND (EO.user_id=USER.id) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*CLOSED ORDERS - BASE CURRENCY - ACCEPTOR */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                    WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "               0, 0, 0, " +
                "               0, 0, " +
                "               IF(EO.operation_type_id = 4, EO.amount_base, 0), IF(EO.operation_type_id = 3, EO.amount_base, 0), 0, 0 " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                "               JOIN EXORDERS EO ON (EO.currency_pair_id = CP1.id) AND (EO.status_id = 3) " +
                "                                   AND (EO.user_acceptor_id=USER.id) AND EO.counter_order_id IS NULL " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*CLOSED ORDERS - CONVERT CURRENCY - ACCEPTOR */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                    WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "               0, 0, 0, " +
                "               0, 0, " +
                "               IF(EO.operation_type_id = 3, EO.amount_convert, 0), IF(EO.operation_type_id = 4, EO.amount_convert, 0), 0, 0 " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                "               JOIN EXORDERS EO ON (EO.currency_pair_id = CP2.id) AND (EO.status_id = 3) " +
                "                                   AND (EO.user_acceptor_id=USER.id) AND EO.counter_order_id IS NULL " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1" +


                "             /*INPUT - CONFIRMED */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
                "                    WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance, " +
                "                    0, 0, 0, " +
                "                    0, 0, " +
                "                    0, 0, IFNULL(RR.amount, 0), 0 " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               LEFT JOIN REFILL_REQUEST RR ON RR.user_id = USER.id AND RR.currency_id = CURRENCY.id AND RR.status_id IN (:refill_success_status_id_list) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +


                "             /*OUTPUT - CONFIRMED */ " +
                "             UNION ALL " +
                "             SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name,  WALLET.active_balance, WALLET.reserved_balance, " +
                "               0, 0, 0, " +
                "               0, 0, " +
                "               0, 0, 0, IFNULL(WITHDRAW_REQUEST.amount, 0) " +
                "             FROM USER " +
                "               JOIN WALLET ON (WALLET.user_id = USER.id) " +
                "               LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                "               JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.user_id = USER.id AND WITHDRAW_REQUEST.currency_id = WALLET.currency_id " +
                "               AND WITHDRAW_REQUEST.status_id IN (:withdraw_success_status_id_list) " +
                "             WHERE USER.id = :id AND CURRENCY.hidden != 1 " +
                "             ) W " +
                "             GROUP BY wallet_id, user_id, currency_id, currency_name, active_balance, reserved_balance " +
                "                ORDER BY currency_name ASC; ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        params.put("withdraw_status_id_list", withdrawEndStatusIds);
        params.put("withdraw_success_status_id_list", withdrawSuccessStatusIds);
        params.put("refill_success_status_id_list", refillSuccessStatusIds);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {

            WalletFormattedDto dto = new WalletFormattedDto();
            dto.setId(rs.getInt("wallet_id"));
            dto.setName(rs.getString("currency_name"));
            dto.setActiveBalance(rs.getBigDecimal("active_balance"));
            dto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
            dto.setReserveOrders(rs.getBigDecimal("reserved_balance_by_orders"));
            dto.setReserveWithdraw(rs.getBigDecimal("reserved_balance_by_withdraw"));
            dto.setTotalInput(rs.getBigDecimal("total_input"));
            dto.setTotalOutput(rs.getBigDecimal("total_output"));
            dto.setTotalSell(rs.getBigDecimal("total_sell"));
            dto.setTotalBuy(rs.getBigDecimal("total_buy"));

            return dto;
        });

    }

    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, List<Integer> withdrawStatusIds, Locale locale) {
        String currencyFilterClause = currencyIds.isEmpty() ? "" : " AND WALLET.currency_id IN(:currencyIds)";
        final String sql =
                " SELECT wallet_id, user_id, currency_id, currency_name, currency_description, active_balance, reserved_balance, " +
                        "   SUM(amount_base+amount_convert+commission_fixed_amount) AS reserved_balance_by_orders, " +
                        "   SUM(withdraw_amount) AS reserved_balance_by_withdraw, " +
                        "   SUM(input_confirmation_amount+input_confirmation_commission) AS on_input_cofirmation, " +
                        "   SUM(input_confirmation_stage) AS input_confirmation_stage, SUM(input_count) AS input_count" +
                        " FROM " +
                        " ( " +
                        " SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, CURRENCY.description AS currency_description, " +
                        "WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance,   " +
                        " IFNULL(SELL.amount_base,0) as amount_base, 0 as amount_convert, 0 AS commission_fixed_amount, " +
                        " 0 AS withdraw_amount, 0 AS withdraw_commission,  " +
                        " 0 AS input_confirmation_amount, 0 AS input_confirmation_commission, 0 AS input_confirmation_stage, 0 AS input_count  " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " LEFT JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                        " LEFT JOIN EXORDERS SELL ON (SELL.operation_type_id=3) AND (SELL.user_id=USER.id) AND (SELL.currency_pair_id = CP1.id) AND (SELL.status_id = 2) " +
                        " WHERE USER.email =  :email AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, CURRENCY.description, WALLET.active_balance, " +
                        " WALLET.reserved_balance,   " +
                        " IFNULL(SOSELL.amount_base,0), 0, 0, " +
                        " 0, 0,  " +
                        " 0, 0, 0, 0  " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " LEFT JOIN CURRENCY_PAIR CP1 ON (CP1.currency1_id = WALLET.currency_id) " +
                        " LEFT JOIN STOP_ORDERS SOSELL ON (SOSELL.operation_type_id=3) AND (SOSELL.user_id=USER.id) AND (SOSELL.currency_pair_id = CP1.id) AND (SOSELL.status_id = 2) " +
                        " WHERE USER.email =  :email AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, CURRENCY.description, WALLET.active_balance, WALLET.reserved_balance,   " +
                        " 0, IFNULL(SOBUY.amount_convert,0), IFNULL(SOBUY.commission_fixed_amount,0), " +
                        " 0, 0, " +
                        " 0, 0, 0, 0 " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " LEFT JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                        " LEFT JOIN STOP_ORDERS SOBUY ON (SOBUY.operation_type_id=4) AND (SOBUY.user_id=USER.id) AND (SOBUY.currency_pair_id = CP2.id) AND (SOBUY.status_id = 2) " +
                        " WHERE USER.email =  :email  AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, CURRENCY.description, WALLET.active_balance, WALLET.reserved_balance,   " +
                        " 0, IFNULL(BUY.amount_convert,0), IFNULL(BUY.commission_fixed_amount,0), " +
                        " 0, 0, " +
                        " 0, 0, 0, 0 " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " LEFT JOIN CURRENCY_PAIR CP2 ON (CP2.currency2_id = WALLET.currency_id) " +
                        " LEFT JOIN EXORDERS BUY ON (BUY.operation_type_id=4) AND (BUY.user_id=USER.id) AND (BUY.currency_pair_id = CP2.id) AND (BUY.status_id = 2) " +
                        " WHERE USER.email =  :email  AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, CURRENCY.description, WALLET.active_balance, WALLET.reserved_balance,   " +
                        " 0, 0, 0, " +
                        " IFNULL(WITHDRAW_REQUEST.amount, 0), IFNULL(WITHDRAW_REQUEST.commission, 0), " +
                        " 0, 0, 0, 0 " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " JOIN WITHDRAW_REQUEST ON WITHDRAW_REQUEST.user_id = USER.id AND WITHDRAW_REQUEST.currency_id = WALLET.currency_id AND WITHDRAW_REQUEST.status_id NOT IN (:status_id_list) " +
                        " WHERE USER.email =  :email AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id, WALLET.user_id, CURRENCY.id, CURRENCY.name, CURRENCY.description, WALLET.active_balance, WALLET.reserved_balance,   " +
                        " 0, 0, 0, " +
                        " IFNULL(TRANSFER_REQUEST.amount, 0), IFNULL(TRANSFER_REQUEST.commission, 0), " +
                        " 0, 0, 0, 0 " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " LEFT JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " JOIN TRANSFER_REQUEST ON TRANSFER_REQUEST.user_id = USER.id AND TRANSFER_REQUEST.currency_id = WALLET.currency_id AND TRANSFER_REQUEST.status_id = 4 " +
                        " WHERE USER.email =  :email AND CURRENCY.hidden != 1 " + currencyFilterClause +
                        "  " +
                        " UNION ALL " +
                        "  " +
                        " SELECT WALLET.id AS wallet_id, WALLET.user_id AS user_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, CURRENCY.description AS currency_description, " +
                        " WALLET.active_balance AS active_balance, WALLET.reserved_balance AS reserved_balance,   " +
                        " 0 AS amount_base, 0 AS amount_convert, 0 AS commission_fixed_amount, " +
                        " 0 AS withdraw_amount, 0 AS withdraw_commission,  " +
                        " SUM(RR.amount), 0, 0, COUNT(RR.id) " +
                        " FROM USER " +
                        " JOIN WALLET ON (WALLET.user_id = USER.id)  " +
                        " JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                        " JOIN REFILL_REQUEST RR ON RR.user_id = USER.id AND RR.currency_id = CURRENCY.id AND RR.status_id = 6 " +
                        " WHERE USER.email =  :email  AND CURRENCY.hidden != 1" + currencyFilterClause +
                        " GROUP BY wallet_id, user_id, currency_id, currency_name,  active_balance, reserved_balance, " +
                        "          amount_base, amount_convert, commission_fixed_amount, " +
                        "          withdraw_amount, withdraw_commission " +
                        " ) W " +
                        " GROUP BY wallet_id, user_id, currency_id, currency_name, currency_description, active_balance, reserved_balance " +
                        "ORDER BY currency_name ASC ";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("email", email);
            put("currencyIds", currencyIds);
            put("status_id_list", withdrawStatusIds);
        }};
        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            MyWalletsDetailedDto myWalletsDetailedDto = new MyWalletsDetailedDto();
            myWalletsDetailedDto.setId(rs.getInt("wallet_id"));
            myWalletsDetailedDto.setUserId(rs.getInt("user_id"));
            myWalletsDetailedDto.setCurrencyId(rs.getInt("currency_id"));
            myWalletsDetailedDto.setCurrencyName(rs.getString("currency_name"));
            myWalletsDetailedDto.setCurrencyDescription(rs.getString("currency_description"));
            myWalletsDetailedDto.setActiveBalance(BigDecimalProcessing.formatLocale(rs.getBigDecimal("active_balance"), locale, 2));
            myWalletsDetailedDto.setOnConfirmation(BigDecimalProcessing.formatLocale(rs.getBigDecimal("on_input_cofirmation"), locale, 2));
            myWalletsDetailedDto.setOnConfirmationStage(BigDecimalProcessing.formatLocale(rs.getBigDecimal("input_confirmation_stage"), locale, 0));
            myWalletsDetailedDto.setOnConfirmationCount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("input_count"), locale, 0));
            myWalletsDetailedDto.setReservedBalance(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance"), locale, 2));
            myWalletsDetailedDto.setReservedByOrders(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance_by_orders"), locale, 2));
            myWalletsDetailedDto.setReservedByMerchant(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance_by_withdraw"), locale, 2));
            return myWalletsDetailedDto;
        });
    }

    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> withdrawStatusIds, Locale locale) {
        return getAllWalletsForUserDetailed(email, Collections.EMPTY_LIST, withdrawStatusIds, locale);
    }

    @Override
    public List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale) {
        final String sql =
                " SELECT TRANSACTION.amount, TRANSACTION.commission_amount, TRANSACTION.amount+TRANSACTION.commission_amount AS total, TRANSACTION.confirmation " +
                        "  FROM WALLET  " +
                        "  JOIN TRANSACTION ON (TRANSACTION.operation_type_id=1) AND (TRANSACTION.user_wallet_id = WALLET.id) AND (TRANSACTION.confirmation BETWEEN 0 AND 3) " +
                        "  JOIN PENDING_PAYMENT ON TRANSACTION.id = PENDING_PAYMENT.invoice_id AND PENDING_PAYMENT.pending_payment_status_id = 6" +
                        "  WHERE WALLET.id = :wallet_id";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("wallet_id", walletId);
        }};
        return jdbcTemplate.query(sql, params, new RowMapper<MyWalletConfirmationDetailDto>() {
            @Override
            public MyWalletConfirmationDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                MyWalletConfirmationDetailDto myWalletConfirmationDetailDto = new MyWalletConfirmationDetailDto();
                myWalletConfirmationDetailDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
                myWalletConfirmationDetailDto.setCommission(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
                myWalletConfirmationDetailDto.setTotal(BigDecimalProcessing.formatLocale(rs.getBigDecimal("total"), locale, 2));
                myWalletConfirmationDetailDto.setStage(BigDecimalProcessing.formatLocale(rs.getBigDecimal("confirmation"), locale, 0));
                return myWalletConfirmationDetailDto;
            }
        });
    }

    @Override
    public Wallet findByUserAndCurrency(int userId, int currencyId) {
        final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET INNER JOIN CURRENCY On" +
                "  WALLET.currency_id = CURRENCY.id WHERE user_id = :userId and currency_id = :currencyId";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("userId", userId);
                put("currencyId", currencyId);
            }
        };
        try {
            return jdbcTemplate.queryForObject(sql, params, walletRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Wallet findById(Integer walletId) {
        final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name " +
                "FROM WALLET " +
                "INNER JOIN CURRENCY ON WALLET.currency_id = CURRENCY.id " +
                "WHERE WALLET.id = :id";
        final Map<String, Integer> params = Collections.singletonMap("id", walletId);
        return jdbcTemplate.queryForObject(sql, params, walletRowMapper);
    }

    @Override
    public Wallet createWallet(User user, int currencyId) {
        final String sql = "INSERT INTO WALLET (currency_id,user_id) VALUES(:currId,:userId)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("currId", currencyId)
                .addValue("userId", user.getId());
        if (jdbcTemplate.update(sql, parameters, keyHolder) > 0) {
            Wallet wallet = new Wallet();
            wallet.setActiveBalance(BigDecimal.valueOf(0));
            wallet.setReservedBalance(BigDecimal.valueOf(0));
            wallet.setId(keyHolder.getKey().intValue());
            wallet.setCurrencyId(currencyId);
            return wallet;
        }
        return null;
    }


    public int getUserIdFromWallet(int walletId) {
        final String sql = "SELECT user_id FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
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
                " FOR UPDATE "; //FOR UPDATE !Impotant
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

    @Override
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
        String sql = "SELECT WALLET.id AS wallet_id, WALLET.currency_id, WALLET.active_balance, WALLET.reserved_balance" +
                "  FROM WALLET " +
                "  WHERE WALLET.id = :walletId " +
                "  FOR UPDATE"; //FOR UPDATE Important!
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        Wallet wallet = null;
        try {
            wallet = jdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Wallet>() {
                @Override
                public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Wallet result = new Wallet();
                    result.setId(rs.getInt("wallet_id"));
                    result.setCurrencyId(rs.getInt("currency_id"));
                    result.setActiveBalance(rs.getBigDecimal("active_balance"));
                    result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                    return result;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }
        /**/
        BigDecimal newActiveBalance = BigDecimalProcessing.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessing.doAction(wallet.getReservedBalance(), amount, ActionType.SUBTRACT);
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            log.error(String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessing.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessing.formatNonePoint(newReservedBalance, false)));
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
            log.error(e);
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        /**/
        return WalletTransferStatus.SUCCESS;
    }


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
                "  FOR UPDATE"; //FOR UPDATE Important!
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletOperationData.getWalletId()));
        Wallet wallet = null;
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
            newActiveBalance = BigDecimalProcessing.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = wallet.getReservedBalance();
        } else {
            newActiveBalance = wallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessing.doAction(wallet.getReservedBalance(), amount, ActionType.ADD);
        }
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            log.error(String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessing.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessing.formatNonePoint(newReservedBalance, false)));
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

    @Override
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
                " FOR UPDATE "; //FOR UPDATE !Impotant

        Map<String, Object> params = new HashMap<>();
        params.put("order_id", orderId);
        params.put("currency_id", operationType == SELL ? currencyPair.getCurrency1().getId() : currencyPair.getCurrency2().getId());
        try {
            return jdbcTemplate.queryForObject(sql, params, getWalletsForOrderCancelDtoMapper(operationType));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId) {
        CurrencyPair currencyPair = currencyDao.findCurrencyPairById(currencyPairId);
        String sql = "SELECT " +
                " SO.id AS order_id, " +
                " SO.status_id AS order_status_id, " +
                " SO.amount_base AS amount_base, " +
                " SO.amount_convert AS amount_convert, " +
                " SO.commission_fixed_amount AS commission_fixed_amount, " +
                " WA.id AS wallet_id, " +
                " WA.active_balance AS active_balance, " +
                " WA.reserved_balance AS reserved_balance " +
                " FROM STOP_ORDERS AS SO " +
                " JOIN WALLET AS WA ON  (WA.user_id = SO.user_id) AND " +
                "             (WA.currency_id = :currency_id) " +
                " WHERE (SO.id = :order_id)" +
                " FOR UPDATE "; //FOR UPDATE !Impotant
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        namedParameters.put("currency_id", operationType == SELL ? currencyPair.getCurrency1().getId() : currencyPair.getCurrency2().getId());
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, getWalletsForOrderCancelDtoMapper(operationType));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
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
                        "  FOR UPDATE "; //FOR UPDATE !Important
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("deleted_order_id", orderId);
            put("currency1_id", currencyPair.getCurrency1().getId());
            put("currency2_id", currencyPair.getCurrency2().getId());
        }};
        return jdbcTemplate.query(sql, namedParameters, new RowMapper<OrderDetailDto>() {
            @Override
            public OrderDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer operationTypeId = rs.getInt("operation_type_id");
                BigDecimal orderCreatorReservedAmount = operationTypeId == 3 ? rs.getBigDecimal("amount_base") :
                        BigDecimalProcessing.doAction(rs.getBigDecimal("amount_convert"), rs.getBigDecimal("commission_fixed_amount"),
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
            }
        });
    }

    @Override
    public void addToWalletBalance(Integer walletId, BigDecimal addedAmountActive, BigDecimal addedAmountReserved) {
        String sql = "UPDATE WALLET SET active_balance = active_balance + :add_active, " +
                "reserved_balance = reserved_balance + :add_reserved WHERE id = :id";
        Map<String, Number> params = new HashMap<>();
        params.put("id", walletId);
        params.put("add_active", addedAmountActive);
        params.put("add_reserved", addedAmountReserved);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<UserWalletSummaryDto> getUsersWalletsSummaryNew(Integer requesterUserId, List<Integer> roleIds) {
        String where = " WHERE roleid ";
        String and = " AND roleid ";
        String roleList = " IN (:role_list) ";
        String exists = " AGRIGATE WHERE EXISTS (SELECT * FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP " +
                " WHERE (IOP.currency_id = AGRIGATE.currency_id AND (IOP.user_id = :requester_user_id)) ) " +
                "  GROUP BY ROLE_ID, CURRENCY_ID, CURRENCY_NAME ";

        String sql = "SELECT " +
                "   role_id, currency_id, currency_name, " +
                "   sum(wallet_count) AS wallet_count, sum(active_balance) AS active_balance, sum(reserved_balance) AS reserved_balance, " +
                "   sum(merchant_amount_input) AS merchant_amount_input, sum(merchant_amount_output) AS merchant_amount_output" +
                "  FROM " +
                "  ( " +
                "      ( " +
                "      SELECT USER.roleid AS role_id, CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, 0 AS wallet_count, 0 AS active_balance, 0 AS reserved_balance, SUM(TX.amount) AS merchant_amount_input, 0 AS merchant_amount_output " +
                "      FROM TRANSACTION TX " +
                "      JOIN WALLET ON (WALLET.id = TX.user_wallet_id) " +
                "      JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) AND (CURRENCY.HIDDEN != 1) " +
                "      JOIN USER ON (USER.ID = WALLET.USER_ID) " +
                "      WHERE  TX.PROVIDED = 1 " +
                "      AND TX.status_id=1         " +
                "      AND TX.source_type IN ('REFILL')        " +
                "      AND TX.OPERATION_TYPE_ID = 1 " +
                (!roleIds.isEmpty() ? and + " " + roleList : " ") +
                "      GROUP BY USER.roleid, CURRENCY.id, CURRENCY.name " +
                "      ) " +
                "    UNION " +
                "      ( " +
                "      SELECT USER.roleid, CURRENCY.id, CURRENCY.name, 0, 0, 0, 0, SUM(TX.amount) " +
                "      FROM TRANSACTION TX " +
                "      JOIN WALLET ON (WALLET.id = TX.user_wallet_id) " +
                "      JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) AND (CURRENCY.HIDDEN != 1) " +
                "      JOIN USER ON (USER.ID = WALLET.USER_ID) " +
                "      WHERE  TX.PROVIDED = 1 " +
                "      AND TX.status_id=1         " +
                "      AND TX.source_type IN ('WITHDRAW')        " +
                "      AND TX.OPERATION_TYPE_ID = 2 " +
                (!roleIds.isEmpty() ? and + " " + roleList : " ") +
                "      GROUP BY USER.roleid, CURRENCY.id, CURRENCY.name " +
                "      ) " +
                "    UNION " +
                "      ( " +
                "      SELECT USER.roleid, CURRENCY.id, CURRENCY.name, COUNT(*), SUM(active_balance), SUM(reserved_balance), 0, 0 " +
                "      FROM WALLET " +
                "      STRAIGHT_JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) AND (CURRENCY.hidden != 1) " +
                "      STRAIGHT_JOIN USER ON (USER.id = WALLET.user_id) " +
                (!roleIds.isEmpty() ? where + " " + roleList : " ") +
                "      GROUP BY USER.roleid, CURRENCY.id, CURRENCY.name " +
                "      ) " +
                "  ) " + exists;

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("requester_user_id", requesterUserId);
            if (!roleIds.isEmpty()) {
                put("role_list", roleIds);
            }
        }};

        ArrayList<UserWalletSummaryDto> result = (ArrayList<UserWalletSummaryDto>) slaveJdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<UserWalletSummaryDto>() {
            @Override
            public UserWalletSummaryDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UserWalletSummaryDto userWalletSummaryDto = new UserWalletSummaryDto();
                userWalletSummaryDto.setUserRoleId(rs.getInt("role_id"));
                userWalletSummaryDto.setCurrencyName(rs.getString("currency_name"));
                userWalletSummaryDto.setWalletsAmount(rs.getInt("wallet_count"));
                userWalletSummaryDto.setActiveBalance(rs.getBigDecimal("active_balance"));
                userWalletSummaryDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                userWalletSummaryDto.setMerchantAmountInput(rs.getBigDecimal("merchant_amount_input"));
                userWalletSummaryDto.setMerchantAmountOutput(rs.getBigDecimal("merchant_amount_output"));
                return userWalletSummaryDto;
            }
        });
        return result;
    }

    @Override
    public boolean isUserAllowedToManuallyChangeWalletBalance(int adminId, int walletHolderUserId) {
        String sql = "SELECT user_id FROM USER_ADMIN_AUTHORITY_ROLE_APPLICATION " +
                "WHERE admin_authority_id = 8 AND user_id = :admin_id AND applied_to_role_id = " +
                "(SELECT roleid FROM USER where id = :user_holder_id) ";
        Map<String, Integer> params = new HashMap<String, Integer>() {{
            put("admin_id", adminId);
            put("user_holder_id", walletHolderUserId);
        }};
        return jdbcTemplate.queryForList(sql, params, Integer.class).size() > 0;
    }


    @Override
    public List<UserGroupBalanceDto> getWalletBalancesSummaryByGroups() {
        String sql = "SELECT CUR.name AS currency_name, CUR.id as currency_id, AGR.feature_name, AGR.total_balance FROM ( " +
                "  SELECT STRAIGHT_JOIN W.currency_id, URGF.name AS feature_name, SUM(IFNULL(W.active_balance, 0)) + SUM(IFNULL(W.reserved_balance, 0)) AS total_balance " +
                "  FROM WALLET W " +
                "    JOIN USER U ON U.id = W.user_id " +
                "    JOIN USER_ROLE UR ON U.roleid = UR.id " +
                "    JOIN USER_ROLE_REPORT_GROUP_FEATURE URGF ON UR.user_role_report_group_feature_id = URGF.id " +
                "  GROUP BY W.currency_id, URGF.name " +
                "  ) AGR " +
                "  JOIN CURRENCY CUR ON AGR.currency_id = CUR.id AND CUR.hidden = 0" +
                "  ORDER BY currency_id";
        return slaveJdbcTemplate.query(sql, Collections.emptyMap(), (rs, row) -> {
            UserGroupBalanceDto dto = new UserGroupBalanceDto();
            dto.setCurId(rs.getInt("currency_id"));
            dto.setCurrency(rs.getString("currency_name"));
            dto.setReportGroupUserRole(ReportGroupUserRole.valueOf(rs.getString("feature_name")));
            dto.setTotalBalance(rs.getBigDecimal("total_balance"));
            return dto;
        });
    }

    @Override
    public List<UserRoleBalanceDto> getWalletBalancesSummaryByRoles(List<Integer> roleIdsList) {
        String sql = "SELECT CUR.name AS currency_name, CUR.id AS currency_id, UR.name AS role_name, AGR.total_balance FROM ( " +
                "     SELECT STRAIGHT_JOIN W.currency_id, U.roleid AS role_id, " +
                "     (SUM(W.active_balance) + SUM(W.reserved_balance)) AS total_balance " +
                "      FROM WALLET W " +
                "      JOIN USER U ON U.id = W.user_id " +
                "     WHERE U.roleid IN (:role_list) " +
                "      GROUP BY W.currency_id, U.roleid " +
                "      ) AGR " +
                "  JOIN CURRENCY CUR ON AGR.currency_id = CUR.id  AND CUR.hidden = 0" +
                "  JOIN USER_ROLE UR ON AGR.role_id = UR.id " +
                "  ORDER BY currency_id";
        return slaveJdbcTemplate.query(sql, Collections.singletonMap("role_list", roleIdsList), (rs, row) -> {
            UserRoleBalanceDto dto = new UserRoleBalanceDto();
            dto.setCurrency(rs.getString("currency_name"));
            dto.setUserRole(UserRole.valueOf(rs.getString("role_name")));
            dto.setTotalBalance(rs.getBigDecimal("total_balance"));
            //wolper 19.04.18
            //currency id added
            dto.setCurId(rs.getInt("currency_id"));
            return dto;
        });
    }


    @Override
    public List<ExternalWalletBalancesDto> getExternalMainWalletBalances() {
        String sql = "SELECT cewb.currency_id, " +
                "cur.name AS currency_name, " +
                "cewb.usd_rate, " +
                "cewb.btc_rate, " +
                "cewb.main_balance, " +
                "cewb.reserved_balance, " +
                "cewb.total_balance, " +
                "cewb.total_balance_usd, " +
                "cewb.total_balance_btc, " +
                "cewb.last_updated_at" +
                " FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " JOIN CURRENCY cur on (cewb.currency_id = cur.id AND cur.hidden = 0)" +
                " ORDER BY cewb.currency_id";
        return slaveJdbcTemplate.query(sql, (rs, row) -> ExternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .mainBalance(rs.getBigDecimal("main_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.usd_rate = :usd_rate, cewb.btc_rate = :btc_rate, " +
                "cewb.main_balance = IFNULL(:main_balance, 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_id = :currency_id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", externalWalletBalancesDto.getCurrencyId());
                put("usd_rate", externalWalletBalancesDto.getUsdRate());
                put("btc_rate", externalWalletBalancesDto.getBtcRate());
                put("main_balance", externalWalletBalancesDto.getMainBalance());
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        String sql = "SELECT iwb.currency_id, " +
                "cur.name AS currency_name, " +
                "iwb.role_id, " +
                "ur.name AS role_name, " +
                "iwb.usd_rate, " +
                "iwb.btc_rate, " +
                "iwb.total_balance, " +
                "iwb.total_balance_usd, " +
                "iwb.total_balance_btc, " +
                "iwb.last_updated_at" +
                " FROM INTERNAL_WALLET_BALANCES iwb" +
                " JOIN CURRENCY cur ON (cur.id = iwb.currency_id AND cur.hidden = 0)" +
                " JOIN USER_ROLE ur ON ur.id = iwb.role_id" +
                " ORDER BY iwb.currency_id, iwb.role_id";
        return slaveJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto) {
        final String sql = "UPDATE INTERNAL_WALLET_BALANCES iwb" +
                " SET iwb.usd_rate = :usd_rate, iwb.btc_rate = :btc_rate, " +
                "iwb.total_balance = IFNULL(:total_balance, 0), " +
                "iwb.total_balance_usd = iwb.total_balance * iwb.usd_rate, " +
                "iwb.total_balance_btc = iwb.total_balance * iwb.btc_rate, " +
                "iwb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE iwb.currency_id = :currency_id AND iwb.role_id = :role_id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", internalWalletBalancesDto.getCurrencyId());
                put("role_id", internalWalletBalancesDto.getRoleId());
                put("usd_rate", internalWalletBalancesDto.getUsdRate());
                put("btc_rate", internalWalletBalancesDto.getBtcRate());
                put("total_balance", internalWalletBalancesDto.getTotalBalance());
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void createReservedWalletAddress(int currencyId) {
        final String sql = "INSERT INTO COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS (currency_id) VALUES (:currency_id)";

        jdbcTemplate.update(sql, singletonMap("currency_id", currencyId));
    }

    @Override
    public void deleteReservedWalletAddress(int id, int currencyId) {
        String sql = "DELETE FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS WHERE id = :id";

        jdbcTemplate.update(sql, singletonMap("id", id));

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_id = :currency_id";

        jdbcTemplate.update(sql, singletonMap("currency_id", currencyId));
    }

    @Override
    public void updateReservedWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto) {
        String nameSql = StringUtils.EMPTY;
        if (nonNull(externalReservedWalletAddressDto.getName())) {
            nameSql = "cwera.name = :name,";
        }
        String sql = String.format("UPDATE COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " SET cwera.currency_id = :currency_id, %s cwera.wallet_address = :wallet_address, cwera.balance = :balance" +
                " WHERE cwera.id = :id", nameSql);

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("id", externalReservedWalletAddressDto.getId());
                put("currency_id", externalReservedWalletAddressDto.getCurrencyId());
                put("name", externalReservedWalletAddressDto.getName());
                put("wallet_address", externalReservedWalletAddressDto.getWalletAddress());
                put("balance", externalReservedWalletAddressDto.getBalance());
            }
        };
        jdbcTemplate.update(sql, params);

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_id = :currency_id";

        params = new HashMap<String, Object>() {
            {
                put("currency_id", externalReservedWalletAddressDto.getCurrencyId());
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId) {
        String sql = "SELECT cwera.id, cwera.currency_id, cwera.name, cwera.wallet_address, cwera.balance" +
                " FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " WHERE cwera.currency_id = :currency_id";

        Map<String, String> params = Collections.singletonMap("currency_id", currencyId);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> ExternalReservedWalletAddressDto.builder()
                .id(rs.getInt("id"))
                .currencyId(rs.getInt("currency_id"))
                .name(rs.getString("name"))
                .walletAddress(rs.getString("wallet_address"))
                .balance(rs.getBigDecimal("balance"))
                .build());
    }

    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        String sql = "SELECT cur.id AS currency_id, " +
                "cur.name AS currency_name, " +
                "ur.id AS role_id, " +
                "ur.name AS role_name, " +
                "SUM(w.active_balance + w.reserved_balance) AS total_balance" +
                " FROM WALLET w" +
                " JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.hidden = 0" +
                " JOIN USER u ON u.id = w.user_id" +
                " JOIN USER_ROLE ur ON ur.id = u.roleid" +
                " GROUP BY cur.id, ur.id" +
                " ORDER BY cur.id, ur.id";

        return slaveJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .build());
    }

    @Override
    public BigDecimal retrieveSummaryUSD() {
        String sql = "SELECT SUM(cewb.total_balance_usd) FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb";
        return slaveJdbcTemplate.queryForObject(sql, Collections.emptyMap(), BigDecimal.class);
    }

    @Override
    public BigDecimal retrieveSummaryBTC() {
        String sql = "SELECT SUM(cewb.total_balance_btc) FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb";
        return slaveJdbcTemplate.queryForObject(sql, Collections.emptyMap(), BigDecimal.class);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance) {
        String sql = "UPDATE birzha.COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " SET cwera.balance = :balance" +
                " WHERE cwera.currency_id = :currency_id" +
                " AND cwera.wallet_address = :wallet_address";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
                put("wallet_address", walletAddress);
                put("balance", balance);
            }
        };
        jdbcTemplate.update(sql, params);

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_id = :currency_id";

        params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
            }
        };
        jdbcTemplate.update(sql, params);
    }
}