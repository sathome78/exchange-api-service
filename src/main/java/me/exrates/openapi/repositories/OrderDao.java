package me.exrates.openapi.repositories;

import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.dto.CandleChartItemDto;
import me.exrates.openapi.models.dto.CoinmarketApiDto;
import me.exrates.openapi.models.dto.TradeHistoryDto;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.dto.UserTradeHistoryDto;
import me.exrates.openapi.models.dto.WalletsAndCommissionsDto;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionDto;
import me.exrates.openapi.models.dto.openAPI.OpenOrderDto;
import me.exrates.openapi.models.dto.openAPI.OrderBookItem;
import me.exrates.openapi.models.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.models.vo.BackDealInterval;
import me.exrates.openapi.repositories.callbacks.StoredProcedureCallback;
import me.exrates.openapi.repositories.mappers.OrderRowMapper;
import me.exrates.openapi.repositories.mappers.CommissionRowMapper;
import me.exrates.openapi.repositories.mappers.OrderBookItemRowMapper;
import me.exrates.openapi.repositories.mappers.TradeHistoryRowMapper;
import me.exrates.openapi.repositories.mappers.TransactionRowMapper;
import me.exrates.openapi.repositories.mappers.UserOrdersRowMapper;
import me.exrates.openapi.repositories.mappers.UserTradeHistoryRowMapper;
import me.exrates.openapi.repositories.mappers.WalletsAndCommissionsRowMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static me.exrates.openapi.models.enums.OperationType.INPUT;
import static me.exrates.openapi.models.enums.OperationType.OUTPUT;
import static me.exrates.openapi.models.enums.OrderStatus.CLOSED;
import static me.exrates.openapi.models.enums.OrderStatus.OPENED;
import static me.exrates.openapi.models.enums.TransactionSourceType.ORDER;

@Repository
public class OrderDao {

    private static final String CALL_GET_COINMARKETCAP_STATISTICS_PROCEDURE_SQL = "{call GET_COINMARKETCAP_STATISTICS('%s')}";
    private static final String CALL_GET_DATA_FOR_CANDLE_SQL = "{call GET_DATA_FOR_CANDLE(NOW(), %d, '%s', %d)}";

    private static final String GET_ORDER_BOOK_ITEMS_BY_TYPE_SQL = "SELECT o.operation_type_id, o.amount_base as amount, o.exrate as price" +
            " FROM EXORDERS o" +
            " WHERE o.currency_pair_id = :currency_pair_id" +
            " AND o.status_id = :status_id AND o.operation_type_id = :operation_type_id" +
            " ORDER BY o.exrate";

    private static final String GET_ORDER_BOOK_ITEMS_SQL = "SELECT o.operation_type_id, o.amount_base as amount, o.exrate as price" +
            " FROM EXORDERS o" +
            " WHERE o.currency_pair_id = :currency_pair_id AND o.status_id = :status_id";

    private static final String GET_TRADE_HISTORY_SQL = "SELECT o.id as order_id, o.date_creation as created, o.date_acception as accepted, " +
            "o.amount_base as amount, o.exrate as price, o.amount_convert as sum, c.value as commission, o.operation_type_id" +
            " FROM EXORDERS o" +
            " JOIN COMMISSION c on o.commission_id = c.id" +
            " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
            " AND o.date_acception BETWEEN :start_date AND :end_date" +
            " ORDER BY o.date_acception ASC";

    private static final String GET_USER_ORDERS_BY_STATUS_SQL = "SELECT o.id AS order_id, o.amount_base AS amount, o.exrate AS price, " +
            "cp.name AS currency_pair_name, o.operation_type_id, o.date_creation AS created, o.date_acception AS accepted" +
            " FROM EXORDERS o" +
            " JOIN CURRENCY_PAIR cp ON o.currency_pair_id = cp.id " +
            " WHERE o.user_id = :user_id AND o.status_id = :status_id";

    private static final String GET_ALL_COMMISSIONS_SQL = "SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) AS buy_commission, " +
            "SUM(input_commission) AS input_commission, SUM(output_commission) AS output_commission, SUM(transfer_commission) AS transfer_commission" +
            "  FROM " +
            "      ((SELECT sell.value AS sell_commission, 0 AS buy_commission, 0 AS input_commission, 0 AS output_commission, 0 AS transfer_commission " +
            "      FROM COMMISSION sell " +
            "      WHERE sell.operation_type = 3 AND sell.user_role = :user_role " +
            "      ORDER BY sell.date DESC LIMIT 1)  " +
            "    UNION " +
            "      (SELECT 0 AS sell_commission, buy.value AS buy_commission, 0 AS input_commission, 0 AS output_commission, 0 AS transfer_commission " +
            "      FROM COMMISSION buy " +
            "      WHERE buy.operation_type = 4 AND buy.user_role = :user_role " +
            "      ORDER BY buy.date DESC LIMIT 1) " +
            "    UNION " +
            "      (SELECT 0 AS sell_commission, 0 AS buy_commission, input.value AS input_commission, 0 AS output_commission, 0 AS transfer_commission  " +
            "      FROM COMMISSION input " +
            "      WHERE input.operation_type = 1 AND input.user_role = :user_role " +
            "      ORDER BY input.date DESC LIMIT 1) " +
            "    UNION " +
            "      (SELECT 0 AS sell_commission, 0 AS buy_commission, 0 AS input_commission, output.value AS output_commission, 0 AS transfer_commission  " +
            "      FROM COMMISSION output " +
            "      WHERE output.operation_type = 2 AND output.user_role = :user_role  " +
            "      ORDER BY output.date DESC LIMIT 1) " +
            "    UNION " +
            "      (SELECT 0 AS sell_commission, 0 AS buy_commission, 0 AS input_commission, 0 AS output_commission, transfer.value AS transfer_commission " +
            "      FROM COMMISSION transfer " +
            "      WHERE transfer.operation_type = 9 AND transfer.user_role = :user_role  " +
            "      ORDER BY transfer.date DESC LIMIT 1) " +
            "  ) COMMISSION";

    private static final String GET_USER_TRADE_HISTORY_BY_CURRENCY_PAIR_SQL = "SELECT o.id as order_id, o.user_id as user_id, o.date_creation as created, " +
            "o.date_acception as accepted, o.amount_base as amount, o.exrate as price, o.amount_convert as sum, c.value as commission, o.operation_type_id" +
            " FROM EXORDERS o" +
            " JOIN COMMISSION c on o.commission_id = c.id" +
            " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id) AND o.currency_pair_id = :currency_pair_id" +
            " AND o.status_id = :status_id AND o.date_acception BETWEEN :start_date AND :end_date" +
            " ORDER BY o.date_acception LIMIT :limit";

    private static final String GET_ORDER_TRANSACTIONS_SQL = "SELECT t.id, t.user_wallet_id, t.amount, t.commission_amount AS commission, " +
            "cur.name AS currency, t.datetime AS time, t.operation_type_id, o.status_id" +
            " FROM TRANSACTION t" +
            " JOIN CURRENCY cur on t.currency_id = cur.id" +
            " JOIN EXORDERS o on o.id = t.source_id" +
            " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id)" +
            " AND t.source_id = :order_id" +
            " AND t.source_type = :source_type" +
            " AND (t.operation_type_id = :operation_type_1 OR t.operation_type_id = :operation_type_2)" +
            " ORDER BY t.id";

    private static final String GET_WALLET_AND_COMMISSION_SQL = "SELECT u.id AS user_id, w.id AS wallet_id, w.active_balance, " +
            "com.id AS commission_id, com.value AS commission_value" +
            " FROM USER u" +
            " LEFT JOIN WALLET w ON (w.user_id = u.id) AND (w.currency_id = :currency_id)" +
            " LEFT JOIN ((SELECT c.id, c.value" +
            "              FROM COMMISSION c" +
            "              WHERE c.operation_type = :operation_type_id AND c.user_role = :user_role" +
            "              ORDER BY c.date DESC" +
            "              LIMIT 1) AS com) ON (1 = 1) " +
            " WHERE u.email = :email";

    private static final String GET_LOWEST_OPEN_ORDER_PRICE_BY_CURRENCY_PAIR_AND_OPERATION_TYPE_SQL = "SELECT o.exrate" +
            " FROM EXORDERS o" +
            " WHERE o.status_id = 2 AND o.currency_pair_id = :currency_pair_id AND o.operation_type_id = :operation_type_id" +
            " ORDER BY o.exrate ASC LIMIT 1";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id, :order_source_id, :base_type)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", OrderStatus.INPROCESS.getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name());
        int result = jdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    //+
    public ExOrder getOrderById(int orderId) {
        String sql = "SELECT * FROM EXORDERS WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, OrderRowMapper.map());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE EXORDERS SET status_id=:status_id WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = jdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    //+
    public boolean updateOrder(ExOrder exOrder) {
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, " +
                " date_acception=NOW()  " +
                " where id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = jdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    //+
    public boolean lockOrdersListForAcception(List<Integer> ordersList) {
        //TODO Why cycle?? not WHERE id IN (...) ?

        for (Integer orderId : ordersList) {
            String sql = "SELECT id " +
                    "  FROM EXORDERS " +
                    "  WHERE id = :order_id " +
                    "  FOR UPDATE ";
            Map<String, Object> namedParameters = new HashMap<>();
            namedParameters.put("order_id", orderId);
            try {
                jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                return false;
            }
        }
        return true;
    }

    //+
    public List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate,
                                         OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType) {
        String sortDirection = "";
        String exrateClause = "";
        if (orderType == OperationType.BUY) {
            sortDirection = "DESC";
            exrateClause = "AND EO.exrate >= :exrate ";
        } else if (orderType == OperationType.SELL) {
            sortDirection = "ASC";
            exrateClause = "AND EO.exrate <= :exrate ";
        }
        String roleJoinClause = sameRoleOnly ? " JOIN USER U ON EO.user_id = U.id AND U.roleid = :acceptor_role_id " :
                "JOIN USER U ON EO.user_id = U.id AND U.roleid IN (SELECT user_role_id FROM USER_ROLE_SETTINGS " +
                        "WHERE user_role_id = :acceptor_role_id OR order_acception_same_role_only = 0)";
        String sqlSetVar = "SET @cumsum := 0";

        /*needs to return several orders with best exrate if their total sum is less than amount in param,
         * or at least one order if base amount is greater than param amount*/
        String sql = "SELECT EO.id, EO.user_id, EO.currency_pair_id, EO.operation_type_id, EO.exrate, EO.amount_base, EO.amount_convert, " +
                "EO.commission_id, EO.commission_fixed_amount, EO.date_creation, EO.status_id, EO.base_type " +
                "FROM EXORDERS EO " + roleJoinClause +
                "WHERE EO.status_id = 2 AND EO.currency_pair_id = :currency_pair_id AND EO.base_type =:order_base_type " +
                "AND EO.operation_type_id = :operation_type_id " + exrateClause +
                " ORDER BY EO.exrate " + sortDirection + ", EO.amount_base ASC ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_pair_id", currencyPairId);
            put("exrate", exrate);
            put("operation_type_id", orderType.getType());
            put("acceptor_role_id", userAcceptorRoleId);
            put("order_base_type", orderBaseType.name());
        }};
        jdbcTemplate.execute(sqlSetVar, PreparedStatement::execute);

        return jdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    //+
    public List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType) {
        String orderByDirection = orderType == OrderType.SELL ? " ASC " : " DESC ";
        String orderBySql = " ORDER BY exrate " + orderByDirection;
        String sql = "SELECT id, operation_type_id, amount_base, exrate FROM EXORDERS " +
                "WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " + orderBySql;
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);
        return jdbcTemplate.query(sql, params, (rs, row) -> {
            OpenOrderDto item = new OpenOrderDto();
            item.setId(rs.getInt("id"));
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name());
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setPrice(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    public List<ExOrder> getAllOpenedOrdersByUserId(Integer userId) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type" +
                " FROM EXORDERS o" +
                " WHERE o.user_id = :user_id AND o.status_id = : status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", OPENED.getStatus());

        return jdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    public List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type" +
                " FROM EXORDERS o" +
                " JOIN CURRENCY_PAIR cp on o.currency_pair_id = cp.id" +
                " WHERE o.user_id = :user_id AND cp.name = :currency_pair AND o.status_id = : status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair", currencyPair);
        params.put("status_id", OPENED.getStatus());

        return jdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    //+
    public List<CoinmarketApiDto> getCoinmarketData(String pairName) {
        return jdbcTemplate.execute(
                String.format(CALL_GET_COINMARKETCAP_STATISTICS_PROCEDURE_SQL, pairName),
                StoredProcedureCallback.getCoinmarketDataCallback());
    }

    //+
    public List<OrderBookItem> getOrderBookItemsByType(Integer currencyPairId, OrderType orderType, Integer limit) {
        String directionSql = orderType == OrderType.BUY ? " DESC" : StringUtils.EMPTY;
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        return jdbcTemplate.query(
                GET_ORDER_BOOK_ITEMS_BY_TYPE_SQL + directionSql + limitSql,
                Map.of(
                        "currency_pair_id", currencyPairId,
                        "status_id", OPENED.getStatus(),
                        "operation_type_id", orderType.getOperationType().getType(),
                        "limit", limit),
                OrderBookItemRowMapper.map());
    }

    //+
    public List<OrderBookItem> getOrderBookItems(Integer currencyPairId, Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        return jdbcTemplate.query(
                GET_ORDER_BOOK_ITEMS_SQL + limitSql,
                Map.of(
                        "currency_pair_id", currencyPairId,
                        "status_id", OPENED.getStatus(),
                        "limit", limit),
                OrderBookItemRowMapper.map());
    }

    //+
    public List<TradeHistoryDto> getTradeHistory(Integer currencyPairId,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate,
                                                 Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        return jdbcTemplate.query(
                GET_TRADE_HISTORY_SQL + limitSql,
                Map.of(
                        "status_id", CLOSED.getStatus(),
                        "currency_pair_id", currencyPairId,
                        "start_date", fromDate,
                        "end_date", toDate,
                        "limit", limit),
                TradeHistoryRowMapper.map());
    }

    //+
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return jdbcTemplate.execute(
                String.format(
                        CALL_GET_DATA_FOR_CANDLE_SQL,
                        backDealInterval.getIntervalValue(),
                        backDealInterval.getIntervalType().name(),
                        currencyPair.getId()),
                StoredProcedureCallback.getDataForCandleChartCallback());
    }

    //+
    public List<UserOrdersDto> getUserOrdersByStatus(Integer userId,
                                                     @Null Integer currencyPairId,
                                                     OrderStatus status,
                                                     @NotNull int limit) {
        String currencyPairSql = nonNull(currencyPairId) ? " AND o.currency_pair_id = :currency_pair_id" : StringUtils.EMPTY;
        String orderBySql = " ORDER BY o.date_creation DESC";
        String limitSql = " LIMIT :limit";

        return jdbcTemplate.query(
                GET_USER_ORDERS_BY_STATUS_SQL + currencyPairSql + orderBySql + limitSql,
                Map.of(
                        "user_id", userId,
                        "currency_pair_id", currencyPairId,
                        "status_id", status.getStatus(),
                        "limit", limit),
                UserOrdersRowMapper.map());
    }

    //+
    public CommissionDto getAllCommissions(UserRole userRole) {
        return jdbcTemplate.queryForObject(
                GET_ALL_COMMISSIONS_SQL,
                Map.of("user_role", userRole.getRole()),
                CommissionRowMapper.map());
    }

    //+
    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId,
                                                                       Integer currencyPairId,
                                                                       LocalDateTime fromDate,
                                                                       LocalDateTime toDate,
                                                                       int limit) {
        return jdbcTemplate.query(
                GET_USER_TRADE_HISTORY_BY_CURRENCY_PAIR_SQL,
                Map.of(
                        "user_id", userId,
                        "status_id", CLOSED.getStatus(),
                        "currency_pair_id", currencyPairId,
                        "start_date", fromDate,
                        "end_date", toDate,
                        "limit", limit),
                UserTradeHistoryRowMapper.map(userId));
    }

    //+
    public List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId) {
        return jdbcTemplate.query(
                GET_ORDER_TRANSACTIONS_SQL,
                Map.of(
                        "user_id", userId,
                        "order_id", orderId,
                        "source_type", ORDER.name(),
                        "operation_type_1", INPUT.getType(),
                        "operation_type_2", OUTPUT.getType()),
                TransactionRowMapper.map());
    }

    //+
    public WalletsAndCommissionsDto getWalletAndCommission(String email,
                                                           Currency currency,
                                                           OperationType operationType,
                                                           UserRole userRole) {
        return jdbcTemplate.queryForObject(
                GET_WALLET_AND_COMMISSION_SQL,
                Map.of(
                        "email", email,
                        "operation_type_id", operationType.getType(),
                        "currency_id", currency.getId(),
                        "user_role", userRole.getRole()),
                WalletsAndCommissionsRowMapper.map());
    }

    //+
    public BigDecimal getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        return jdbcTemplate.queryForObject(
                GET_LOWEST_OPEN_ORDER_PRICE_BY_CURRENCY_PAIR_AND_OPERATION_TYPE_SQL,
                Map.of(
                        "currency_pair_id", currencyPairId,
                        "operation_type_id", operationTypeId),
                BigDecimal.class);
    }
}