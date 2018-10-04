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
import me.exrates.openapi.repositories.mappers.CommissionsRowMapper;
import me.exrates.openapi.repositories.mappers.OpenOrderRowMapper;
import me.exrates.openapi.repositories.mappers.OrderBookItemRowMapper;
import me.exrates.openapi.repositories.mappers.OrderRowMapper;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
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
            " ORDER BY o.exrate %s %s";

    private static final String GET_ORDER_BOOK_ITEMS_SQL = "SELECT o.operation_type_id, o.amount_base as amount, o.exrate as price" +
            " FROM EXORDERS o" +
            " WHERE o.currency_pair_id = :currency_pair_id AND o.status_id = :status_id %s";

    private static final String GET_TRADE_HISTORY_SQL = "SELECT o.id as order_id, o.date_creation as created, o.date_acception as accepted, " +
            "o.amount_base as amount, o.exrate as price, o.amount_convert as sum, c.value as commission, o.operation_type_id" +
            " FROM EXORDERS o" +
            " JOIN COMMISSION c on o.commission_id = c.id" +
            " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
            " AND o.date_acception BETWEEN :start_date AND :end_date" +
            " ORDER BY o.date_acception ASC %s";

    private static final String GET_USER_ORDERS_BY_STATUS_SQL = "SELECT o.id AS order_id, o.amount_base AS amount, o.exrate AS price, " +
            "cp.name AS currency_pair_name, o.operation_type_id, o.date_creation AS created, o.date_acception AS accepted" +
            " FROM EXORDERS o" +
            " JOIN CURRENCY_PAIR cp ON o.currency_pair_id = cp.id " +
            " WHERE o.user_id = :user_id AND o.status_id = :status_id %s %s %s";

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

    private static final String SELECT_TOP_ORDERS_SQL = "SELECT o.id, o.user_id, o.currency_pair_id, o.operation_type_id, o.exrate, " +
            "o.amount_base, o.amount_convert, o.commission_id, o.commission_fixed_amount, o.date_creation, o.status_id, o.base_type" +
            " FROM EXORDERS o %s" +
            " WHERE o.status_id = 2 AND o.currency_pair_id = :currency_pair_id AND o.base_type =:order_base_type" +
            " AND o.operation_type_id = :operation_type_id %s" +
            " ORDER BY o.exrate %s, EO.amount_base ASC ";

    private static final String LOCK_ORDERS_LIST_FOR_ACCEPTANCE_SQL = "SELECT o.id" +
            " FROM EXORDERS o" +
            " WHERE o.id IN (:order_ids)" +
            " FOR UPDATE";

    private static final String GET_ORDER_BY_ID_SQL = "SELECT * FROM EXORDERS WHERE id = :id";

    private static final String UPDATE_ORDER_SQL = "UPDATE EXORDERS o" +
            " SET o.user_acceptor_id = :user_acceptor_id, o.status_id = :status_id, o.date_acception = NOW()" +
            " WHERE o.id = :id";

    private static final String CREATE_ORDER_SQL = "INSERT INTO EXORDERS" +
            " (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
            " VALUES (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id, :order_source_id, :base_type)";

    private static final String UPDATE_ORDER_STATUS_SQL = "UPDATE EXORDERS o" +
            " SET o.status_id = :status_id WHERE o.id = :id";

    private static final String GET_OPENED_ORDERS_BY_CURRENCY_PAIR_SQL = "SELECT * FROM EXORDERS o" +
            " JOIN CURRENCY_PAIR cp on o.currency_pair_id = cp.id" +
            " WHERE o.user_id = :user_id AND cp.name = :currency_pair AND o.status_id = : status_id";

    private static final String GET_OPENED_ORDERS_SQL = "SELECT o.id, o.operation_type_id, o.amount_base, o.exrate" +
            " FROM EXORDERS o" +
            " WHERE o.currency_pair_id = :currency_pair_id AND o.status_id = :status_id AND o.operation_type_id = :operation_type_id %s";

    private static final String GET_ALL_OPENED_ORDERS_SQL = "SELECT o.id AS order_id, o.currency_pair_id, o.operation_type_id, " +
            "o.exrate AS price, o.amount_base AS amount, o.amount_convert AS sum, o.commission_id, o.commission_fixed_amount, " +
            "o.date_creation AS created, o.status_id, o.base_type" +
            " FROM EXORDERS o" +
            " WHERE o.user_id = :user_id AND o.status_id = : status_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CoinmarketApiDto> getCoinmarketData(String pairName) {
        try {
            return jdbcTemplate.execute(
                    String.format(CALL_GET_COINMARKETCAP_STATISTICS_PROCEDURE_SQL, pairName),
                    StoredProcedureCallback.getCoinmarketDataCallback());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<OrderBookItem> getOrderBookItemsByType(Integer currencyPairId, OrderType orderType, Integer limit) {
        String directionSql = orderType == OrderType.BUY ? "DESC" : StringUtils.EMPTY;
        String limitSql = nonNull(limit) ? "LIMIT :limit" : StringUtils.EMPTY;

        try {
            return jdbcTemplate.query(
                    String.format(GET_ORDER_BOOK_ITEMS_BY_TYPE_SQL, directionSql, limitSql),
                    Map.of(
                            "currency_pair_id", currencyPairId,
                            "status_id", OPENED.getStatus(),
                            "operation_type_id", orderType.getOperationType().getType(),
                            "limit", limit),
                    OrderBookItemRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<OrderBookItem> getOrderBookItems(Integer currencyPairId, Integer limit) {
        String limitSql = nonNull(limit) ? "LIMIT :limit" : StringUtils.EMPTY;

        try {
            return jdbcTemplate.query(
                    String.format(GET_ORDER_BOOK_ITEMS_SQL, limitSql),
                    Map.of(
                            "currency_pair_id", currencyPairId,
                            "status_id", OPENED.getStatus(),
                            "limit", limit),
                    OrderBookItemRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<TradeHistoryDto> getTradeHistory(Integer currencyPairId,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate,
                                                 Integer limit) {
        String limitSql = nonNull(limit) ? "LIMIT :limit" : StringUtils.EMPTY;

        try {
            return jdbcTemplate.query(
                    String.format(GET_TRADE_HISTORY_SQL, limitSql),
                    Map.of(
                            "status_id", CLOSED.getStatus(),
                            "currency_pair_id", currencyPairId,
                            "start_date", fromDate,
                            "end_date", toDate,
                            "limit", limit),
                    TradeHistoryRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        try {
            return jdbcTemplate.execute(
                    String.format(
                            CALL_GET_DATA_FOR_CANDLE_SQL,
                            backDealInterval.getIntervalValue(),
                            backDealInterval.getIntervalType().name(),
                            currencyPair.getId()),
                    StoredProcedureCallback.getDataForCandleChartCallback());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<UserOrdersDto> getUserOrdersByStatus(Integer userId,
                                                     @Null Integer currencyPairId,
                                                     OrderStatus status,
                                                     @NotNull int limit) {
        String currencyPairSql = nonNull(currencyPairId) ? "AND o.currency_pair_id = :currency_pair_id" : StringUtils.EMPTY;
        String orderBySql = "ORDER BY o.date_creation DESC";
        String limitSql = "LIMIT :limit";

        try {
            return jdbcTemplate.query(
                    String.format(GET_USER_ORDERS_BY_STATUS_SQL, currencyPairSql, orderBySql, limitSql),
                    Map.of(
                            "user_id", userId,
                            "currency_pair_id", currencyPairId,
                            "status_id", status.getStatus(),
                            "limit", limit),
                    UserOrdersRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public CommissionDto getAllCommissions(UserRole userRole) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_ALL_COMMISSIONS_SQL,
                    Map.of("user_role", userRole.getRole()),
                    CommissionsRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException("Could not get all commissions");
        }
    }

    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId,
                                                                       Integer currencyPairId,
                                                                       LocalDateTime fromDate,
                                                                       LocalDateTime toDate,
                                                                       int limit) {
        try {
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
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId) {
        try {
            return jdbcTemplate.query(
                    GET_ORDER_TRANSACTIONS_SQL,
                    Map.of(
                            "user_id", userId,
                            "order_id", orderId,
                            "source_type", ORDER.name(),
                            "operation_type_1", INPUT.getType(),
                            "operation_type_2", OUTPUT.getType()),
                    TransactionRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public WalletsAndCommissionsDto getWalletAndCommission(String email,
                                                           Currency currency,
                                                           OperationType operationType,
                                                           UserRole userRole) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_WALLET_AND_COMMISSION_SQL,
                    Map.of(
                            "email", email,
                            "operation_type_id", operationType.getType(),
                            "currency_id", currency.getId(),
                            "user_role", userRole.getRole()),
                    WalletsAndCommissionsRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException("Could not get wallets and commissions");
        }
    }

    public BigDecimal getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        return jdbcTemplate.queryForObject(
                GET_LOWEST_OPEN_ORDER_PRICE_BY_CURRENCY_PAIR_AND_OPERATION_TYPE_SQL,
                Map.of(
                        "currency_pair_id", currencyPairId,
                        "operation_type_id", operationTypeId),
                BigDecimal.class);
    }

    public List<ExOrder> selectTopOrders(Integer currencyPairId,
                                         BigDecimal exrate,
                                         OperationType orderType,
                                         boolean sameRoleOnly,
                                         Integer userAcceptorRoleId,
                                         OrderBaseType orderBaseType) {
        String sortDirection;
        String exrateClause;

        switch (orderType) {
            case BUY:
                sortDirection = "DESC";
                exrateClause = "AND o.exrate >= :exrate ";
                break;
            case SELL:
                sortDirection = "ASC";
                exrateClause = "AND o.exrate <= :exrate ";
                break;
            default:
                sortDirection = StringUtils.EMPTY;
                exrateClause = StringUtils.EMPTY;
                break;
        }

        String roleJoinClause = sameRoleOnly
                ? "JOIN USER u ON o.user_id = u.id AND u.roleid = :acceptor_role_id "
                : "JOIN USER u ON o.user_id = u.id" +
                " AND u.roleid IN (SELECT urs.user_role_id FROM USER_ROLE_SETTINGS urs" +
                " WHERE urs.user_role_id = :acceptor_role_id OR urs.order_acception_same_role_only = 0)";

        jdbcTemplate.execute("SET @cumsum := 0", PreparedStatement::execute);

        try {
            return jdbcTemplate.query(
                    String.format(SELECT_TOP_ORDERS_SQL, roleJoinClause, exrateClause, sortDirection),
                    Map.of(
                            "currency_pair_id", currencyPairId,
                            "exrate", exrate,
                            "operation_type_id", orderType.getType(),
                            "acceptor_role_id", userAcceptorRoleId,
                            "order_base_type", orderBaseType.name()),
                    OrderRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public boolean lockOrdersListForAcceptance(List<Integer> orderIds) {
        try {
            jdbcTemplate.queryForObject(
                    LOCK_ORDERS_LIST_FOR_ACCEPTANCE_SQL,
                    Map.of("order_ids", orderIds),
                    Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
        return true;
    }

    public ExOrder getOrderById(int orderId) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_ORDER_BY_ID_SQL,
                    Map.of("id", orderId),
                    OrderRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Order with id = %d do not present", orderId));
        }
    }

    public boolean updateOrder(ExOrder order) {
        int update = jdbcTemplate.update(
                UPDATE_ORDER_SQL,
                Map.of(
                        "user_acceptor_id", order.getUserAcceptorId(),
                        "status_id", order.getStatus().getStatus(),
                        "id", order.getId()));
        return update > 0;
    }

    public int createOrder(ExOrder order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = jdbcTemplate.update(
                CREATE_ORDER_SQL,
                new MapSqlParameterSource()
                        .addValue("user_id", order.getUserId())
                        .addValue("currency_pair_id", order.getCurrencyPairId())
                        .addValue("operation_type_id", order.getOperationType().getType())
                        .addValue("exrate", order.getExRate())
                        .addValue("amount_base", order.getAmountBase())
                        .addValue("amount_convert", order.getAmountConvert())
                        .addValue("commission_id", order.getComissionId())
                        .addValue("commission_fixed_amount", order.getCommissionFixedAmount())
                        .addValue("status_id", OrderStatus.INPROCESS.getStatus())
                        .addValue("order_source_id", order.getSourceId())
                        .addValue("base_type", order.getOrderBaseType().name()),
                keyHolder);

        return update <= 0 ? 0 : Objects.requireNonNull(keyHolder.getKey(), "Key should be present").intValue();
    }

    public boolean setStatus(int orderId, OrderStatus status) {
        int result = jdbcTemplate.update(
                UPDATE_ORDER_STATUS_SQL,
                Map.of(
                        "status_id", status.getStatus(),
                        "id", orderId));
        return result > 0;
    }

    public List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair) {
        try {
            return jdbcTemplate.query(
                    GET_OPENED_ORDERS_BY_CURRENCY_PAIR_SQL,
                    Map.of(
                            "user_id", userId,
                            "currency_pair", currencyPair,
                            "status_id", OPENED.getStatus()),
                    OrderRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType) {
        String orderBySql = orderType == OrderType.SELL ? "ORDER BY o.exrate ASC" : "ORDER BY o.exrate DESC";

        try {
            return jdbcTemplate.query(
                    String.format(GET_OPENED_ORDERS_SQL, orderBySql),
                    Map.of(
                            "currency_pair_id", currencyPairId,
                            "status_id", OPENED.getStatus(),
                            "operation_type_id", orderType.getOperationType().getType()),
                    OpenOrderRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }

    public List<ExOrder> getAllOpenedOrdersByUserId(Integer userId) {
        try {
            return jdbcTemplate.query(
                    GET_ALL_OPENED_ORDERS_SQL,
                    Map.of(
                            "user_id", userId,
                            "status_id", OPENED.getStatus()),
                    OrderRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            return emptyList();
        }
    }
}