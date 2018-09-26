package me.exrates.openapi.dao;

import me.exrates.openapi.dao.jdbc.OrderRowMapper;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.dto.CandleChartItemDto;
import me.exrates.openapi.model.dto.CoinmarketApiDto;
import me.exrates.openapi.model.dto.TradeHistoryDto;
import me.exrates.openapi.model.dto.TransactionDto;
import me.exrates.openapi.model.dto.UserTradeHistoryDto;
import me.exrates.openapi.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.openapi.model.dto.openAPI.OpenOrderDto;
import me.exrates.openapi.model.dto.openAPI.OrderBookItem;
import me.exrates.openapi.model.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.model.enums.ActionType;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.OrderBaseType;
import me.exrates.openapi.model.enums.OrderStatus;
import me.exrates.openapi.model.enums.OrderType;
import me.exrates.openapi.model.enums.TransactionStatus;
import me.exrates.openapi.model.enums.UserRole;
import me.exrates.openapi.model.vo.BackDealInterval;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static me.exrates.openapi.model.enums.OperationType.INPUT;
import static me.exrates.openapi.model.enums.OperationType.OUTPUT;
import static me.exrates.openapi.model.enums.OrderStatus.CLOSED;
import static me.exrates.openapi.model.enums.TransactionSourceType.ORDER;

@Repository
public class OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<UserOrdersDto> userOrdersRowMapper = (rs, row) -> {
        int id = rs.getInt("order_id");
        String currencyPairName = rs.getString("currency_pair_name");
        String orderType = OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name();
        LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();
        Timestamp timestampAcceptance = rs.getTimestamp("date_acception");
        LocalDateTime dateAcceptance = timestampAcceptance == null ? null : timestampAcceptance.toLocalDateTime();
        BigDecimal amount = rs.getBigDecimal("amount_base");
        BigDecimal price = rs.getBigDecimal("exrate");
        return new UserOrdersDto(id, currencyPairName, amount, orderType, price, dateCreation, dateAcceptance);
    };

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
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
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
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
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
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
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
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    //+
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName) {
        String s = "{call GET_COINMARKETCAP_STATISTICS('" + currencyPairName + "')}";
        List<CoinmarketApiDto> result = namedParameterJdbcTemplate.execute(s, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CoinmarketApiDto> list = new ArrayList();
            while (rs.next()) {
                CoinmarketApiDto coinmarketApiDto = new CoinmarketApiDto();
                coinmarketApiDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                coinmarketApiDto.setCurrency_pair_name(rs.getString("currency_pair_name"));
                coinmarketApiDto.setFirst(rs.getBigDecimal("first"));
                coinmarketApiDto.setLast(rs.getBigDecimal("last"));
                coinmarketApiDto.setLowestAsk(rs.getBigDecimal("lowestAsk"));
                coinmarketApiDto.setHighestBid(rs.getBigDecimal("highestBid"));
                coinmarketApiDto.setPercentChange(BigDecimalProcessingUtil.doAction(coinmarketApiDto.getFirst(), coinmarketApiDto.getLast(), ActionType.PERCENT_GROWTH));
                coinmarketApiDto.setBaseVolume(rs.getBigDecimal("baseVolume"));
                coinmarketApiDto.setQuoteVolume(rs.getBigDecimal("quoteVolume"));
                coinmarketApiDto.setIsFrozen(rs.getInt("isFrozen"));
                coinmarketApiDto.setHigh24hr(rs.getBigDecimal("high24hr"));
                coinmarketApiDto.setLow24hr(rs.getBigDecimal("low24hr"));
                list.add(coinmarketApiDto);
            }
            rs.close();
            return list;
        });
        return result;
    }

    //+
    public CommissionsDto getAllCommissions(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission, " +
                        "SUM(input_commission) as input_commission, SUM(output_commission) as output_commission, SUM(transfer_commission) as transfer_commission" +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission, 0 as input_commission, 0 as output_commission, " +
                        " 0 as transfer_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value, 0, 0, 0 " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, INPUT.value, 0, 0  " +
                        "      FROM COMMISSION INPUT " +
                        "      WHERE operation_type = 1  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, OUTPUT.value, 0  " +
                        "      FROM COMMISSION OUTPUT " +
                        "      WHERE operation_type = 2 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, 0, TRANSFER.value " +
                        "      FROM COMMISSION TRANSFER " +
                        "      WHERE operation_type = 9 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                CommissionsDto commissionsDto = new CommissionsDto();
                commissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                commissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                commissionsDto.setInputCommission(rs.getBigDecimal("input_commission"));
                commissionsDto.setOutputCommission(rs.getBigDecimal("output_commission"));
                commissionsDto.setTransferCommission(rs.getBigDecimal("transfer_commission"));
                return commissionsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType, UserRole userRole) {
        String sql = "SELECT USER.id AS user_id, WALLET.id AS wallet_id, WALLET.active_balance, COMM.id AS commission_id, COMM.value AS commission_value" +
                "  FROM USER " +
                "    LEFT JOIN WALLET ON (WALLET.user_id=USER.id) AND (WALLET.currency_id = :currency_id) " +
                "    LEFT JOIN ((SELECT COMMISSION.id, COMMISSION.value " +
                "           FROM COMMISSION " +
                "           WHERE COMMISSION.operation_type=:operation_type_id AND COMMISSION.user_role = :user_role ORDER BY COMMISSION.date " +
                "           DESC LIMIT 1) AS COMM) ON (1=1) " +
                "  WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("operation_type_id", operationType.getType());
        namedParameters.put("currency_id", currency.getId());
        namedParameters.put("user_role", userRole.getRole());
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                WalletsAndCommissionsForOrderCreationDto walletsAndCommissionsForOrderCreationDto = new WalletsAndCommissionsForOrderCreationDto();
                walletsAndCommissionsForOrderCreationDto.setUserId(rs.getInt("user_id"));
                walletsAndCommissionsForOrderCreationDto.setSpendWalletId(rs.getInt("wallet_id"));
                walletsAndCommissionsForOrderCreationDto.setSpendWalletActiveBalance(rs.getBigDecimal("active_balance"));
                walletsAndCommissionsForOrderCreationDto.setCommissionId(rs.getInt("commission_id"));
                walletsAndCommissionsForOrderCreationDto.setCommissionValue(rs.getBigDecimal("commission_value"));
                return walletsAndCommissionsForOrderCreationDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
                namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
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
        namedParameterJdbcTemplate.execute(sqlSetVar, PreparedStatement::execute);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
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
    public List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType) {
        String orderDirection = orderType == OrderType.BUY ? " DESC " : " ASC ";
        String sql = "SELECT amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate " + orderDirection;
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(orderType);
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    //+
    public List<OrderBookItem> getOrderBookItems(Integer currencyPairId) {
        String sql = "SELECT operation_type_id, amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
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
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);
        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            OpenOrderDto item = new OpenOrderDto();
            item.setId(rs.getInt("id"));
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name());
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setPrice(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    public List<TradeHistoryDto> getTradeHistory(Integer currencyPairId,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate,
                                                 Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        String sql = "SELECT o.id as order_id, " +
                "o.date_creation as created, " +
                "o.date_acception as accepted, " +
                "o.amount_base as amount, " +
                "o.exrate as price, " +
                "o.amount_convert as sum, " +
                "c.value as commission, " +
                "o.operation_type_id" +
                " FROM EXORDERS o" +
                " JOIN COMMISSION c on o.commission_id = c.id" +
                " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
                " AND o.date_acception BETWEEN :start_date AND :end_date" +
                " ORDER BY o.date_acception ASC"
                + limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", CLOSED.getStatus());
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            TradeHistoryDto tradeHistoryDto = new TradeHistoryDto();
            tradeHistoryDto.setOrderId(rs.getInt("order_id"));
            tradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            tradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            tradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            tradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            tradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            tradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            tradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return tradeHistoryDto;
        });
    }

    //+
    public List<UserOrdersDto> getUserOpenOrders(Integer userId, @Nullable Integer currencyPairId) {


        String currencyPairSql = currencyPairId == null ? "" : " AND EO.currency_pair_id = :currency_pair_id ";
        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE EO.user_id = :user_id AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC ";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return namedParameterJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    public List<UserOrdersDto> getUserOrdersByStatus(Integer userId,
                                                     Integer currencyPairId,
                                                     OrderStatus status,
                                                     int limit,
                                                     int offset) {
        String currencyPairSql = nonNull(currencyPairId) ? " AND EO.currency_pair_id = :currency_pair_id " : StringUtils.EMPTY;
        String limitSql = limit > 0 ? " LIMIT :limit " : StringUtils.EMPTY;
        String offsetSql = (limit > 0 && offset > 0) ? "OFFSET :offset" : StringUtils.EMPTY;

        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE (EO.user_id = :user_id OR EO.user_acceptor_id = :user_id) AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC " + limitSql + offsetSql;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", status.getStatus());
        params.put("limit", limit);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    //+
    public List<UserOrdersDto> getUserOrdersHistory(Integer userId, @Nullable Integer currencyPairId, int limit, int offset) {

        String limitSql = limit > 0 ? " LIMIT :limit " : "";
        String offsetSql = limit > 0 && offset > 0 ? "OFFSET :offset" : "";

        String currencyPairSql = currencyPairId == null ? "" : " AND EO.currency_pair_id = :currency_pair_id ";
        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE (EO.user_id = :user_id OR EO.user_acceptor_id = :user_id) AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC " + limitSql + offsetSql;
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", CLOSED.getStatus());
        params.put("limit", limit);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    //+
    public Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 2 AND currency_pair_id = :currency_pair_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate ASC  LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("operation_type_id", operationTypeId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
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
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
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

    public List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId) {
        String sql = "SELECT t.id, " +
                "t.user_wallet_id, " +
                "t.amount, " +
                "t.commission_amount AS commission, " +
                "cur.name AS currency, " +
                "t.datetime AS time, " +
                "t.operation_type_id, " +
                "o.status_id" +
                " FROM TRANSACTION t" +
                " JOIN CURRENCY cur on t.currency_id = cur.id" +
                " JOIN EXORDERS o on o.id = t.source_id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id)" +
                " AND t.source_id = :order_id" +
                " AND t.source_type = :source_type" +
                " AND (t.operation_type_id = :operation_type_1 OR t.operation_type_id = :operation_type_2)" +
                " ORDER BY t.id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("order_id", orderId);
        params.put("source_type", ORDER.name());
        params.put("operation_type_1", INPUT.getType());
        params.put("operation_type_2", OUTPUT.getType());

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> TransactionDto.builder()
                .transactionId(rs.getInt("id"))
                .walletId(rs.getInt("user_wallet_id"))
                .amount(rs.getBigDecimal("amount"))
                .commission(rs.getBigDecimal("commission"))
                .currency(rs.getString("currency"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .status(TransactionStatus.convert(rs.getInt("status_id")))
                .build());
    }

    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return getCandleChartData(currencyPair, backDealInterval, "NOW()");
    }

    private List<CandleChartItemDto> getCandleChartData(CurrencyPair currencyPair, BackDealInterval backDealInterval, String startTimeSql) {
        String s = "{call GET_DATA_FOR_CANDLE(" + startTimeSql + ", " + backDealInterval.getIntervalValue() + ", '" + backDealInterval.getIntervalType().name() + "', " + currencyPair.getId() + ")}";
        List<CandleChartItemDto> result = namedParameterJdbcTemplate.execute(s, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = new ArrayList<>();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
                candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        });
        return result;
    }

    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId,
                                                                       Integer currencyPairId,
                                                                       LocalDateTime fromDate,
                                                                       LocalDateTime toDate,
                                                                       Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        String sql = "SELECT o.id as order_id, " +
                "o.user_id as user_id, " +
                "o.date_creation as created, " +
                "o.date_acception as accepted, " +
                "o.amount_base as amount, " +
                "o.exrate as price, " +
                "o.amount_convert as sum, " +
                "c.value as commission, " +
                "o.operation_type_id" +
                " FROM EXORDERS o" +
                " JOIN COMMISSION c on o.commission_id = c.id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id) AND o.currency_pair_id = :currency_pair_id" +
                " AND o.status_id = :status_id AND o.date_acception BETWEEN :start_date AND :end_date" +
                " ORDER BY o.date_acception ASC"
                + limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", CLOSED.getStatus());
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            UserTradeHistoryDto userTradeHistoryDto = new UserTradeHistoryDto();
            userTradeHistoryDto.setUserId(userId);
            userTradeHistoryDto.setIsMaker(userId == rs.getInt("user_id"));
            userTradeHistoryDto.setOrderId(rs.getInt("order_id"));
            userTradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            userTradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            userTradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            userTradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            userTradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            userTradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            userTradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return userTradeHistoryDto;
        });
    }
}