package me.exrates.openapi.dao;

import me.exrates.openapi.dao.jdbc.OrderRowMapper;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.dto.CoinmarketApiDto;
import me.exrates.openapi.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.openapi.model.dto.openAPI.OpenOrderDto;
import me.exrates.openapi.model.dto.openAPI.OrderBookItem;
import me.exrates.openapi.model.dto.openAPI.OrderHistoryItem;
import me.exrates.openapi.model.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.model.enums.ActionType;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.OrderBaseType;
import me.exrates.openapi.model.enums.OrderStatus;
import me.exrates.openapi.model.enums.OrderType;
import me.exrates.openapi.model.enums.UserRole;
import me.exrates.openapi.model.vo.BackDealInterval;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@Repository
public class OrderDao {

    private static final Logger LOGGER = LogManager.getLogger(OrderDao.class);

    private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

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

    //+
    public List<OrderHistoryItem> getRecentOrderHistory(Integer currencyPairId, BackDealInterval interval) {
        String sql = "SELECT id, date_acception, exrate, amount_base, amount_convert, operation_type_id FROM EXORDERS " +
                " WHERE currency_pair_id=:currency_pair_id AND status_id=:status_id " +
                " AND date_acception >= now() - INTERVAL " + interval.getInterval() +
                " ORDER BY date_acception";

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", OrderStatus.CLOSED.getStatus());
        params.put("currency_pair_id", currencyPairId);
        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderHistoryItem item = new OrderHistoryItem();
            item.setOrderId(rs.getInt("id"));
            item.setDateAcceptance(rs.getTimestamp("date_acception").toLocalDateTime());
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setPrice(rs.getBigDecimal("exrate"));
            item.setTotal(rs.getBigDecimal("amount_convert"));
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return item;
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
        params.put("status_id", OrderStatus.CLOSED.getStatus());
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
}