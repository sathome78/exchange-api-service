package me.exrates.openapi.repositories;

import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.repositories.mappers.CurrencyPairInfoItemRowMapper;
import me.exrates.openapi.repositories.mappers.CurrencyPairLimitRowMapper;
import me.exrates.openapi.repositories.mappers.CurrencyPairRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDao {

    private static final String FIND_ACTIVE_CURRENCY_PAIR_ID_BY_NAME_SQL = "SELECT cp.id FROM CURRENCY_PAIR cp WHERE cp.name = :pair_name AND cp.hidden != 1";

    private static final String FIND_ACTIVE_CURRENCY_PAIRS_SQL = "SELECT cp.name FROM CURRENCY_PAIR cp WHERE cp.hidden != 1 ORDER BY cp.name ASC";

    private static final String FIND_CURRENCY_PAIR_BY_NAME_SQL = "SELECT cp.id, cp.currency1_id, cp.currency2_id, cp.name, cp.market, cp.type, " +
            "(SELECT name FROM CURRENCY c WHERE c.id = cp.currency1_id) AS currency1_name, " +
            "(SELECT name FROM CURRENCY c WHERE c.id = cp.currency2_id) AS currency2_name " +
            " FROM CURRENCY_PAIR cp" +
            " WHERE cp.name = :currencyPairName";

    private static final String FIND_CURRENCY_PAIR_BY_ID_SQL = "SELECT cp.id, cp.currency1_id, cp.currency2_id, cp.name, cp.market, cp.type, " +
            "(SELECT name FROM CURRENCY c WHERE c.id = cp.currency1_id) AS currency1_name, " +
            "(SELECT name FROM CURRENCY c WHERE c.id = cp.currency2_id) AS currency2_name " +
            " FROM CURRENCY_PAIR cp" +
            " WHERE cp.name = :currencyPairId";

    private static final String FIND_CURRENCY_PAIR_LIMIT_FOR_ROLE_BY_PAIR_AND_TYPE_SQL = "SELECT cp.id AS currency_pair_id, cp.name AS currency_pair_name, " +
            "cpl.min_rate, cpl.max_rate, cpl.min_amount, cpl.max_amount" +
            " FROM CURRENCY_PAIR_LIMIT cpl " +
            " JOIN CURRENCY_PAIR cp ON cpl.currency_pair_id = cp.id AND cp.hidden != 1" +
            " WHERE cpl.currency_pair_id = :currency_pair_id" +
            " AND cpl.user_role_id = :user_role_id" +
            " AND cpl.order_type_id = :order_type_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public CurrencyPair findCurrencyPairByOrderId(int orderId) {
        String sql = "SELECT CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, name, type," +
                "CURRENCY_PAIR.market, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM EXORDERS " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                " WHERE EXORDERS.id = :order_id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("order_id", String.valueOf(orderId));
        return jdbcTemplate.queryForObject(sql, namedParameters, CurrencyPairRowMapper.map());
    }

    //+
    public Integer findActiveCurrencyPairIdByName(String pairName) {
        Map<String, Object> params = Collections.singletonMap("pair_name", pairName);

        return jdbcTemplate.queryForObject(FIND_ACTIVE_CURRENCY_PAIR_ID_BY_NAME_SQL, params, Integer.class);
    }

    //+
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return jdbcTemplate.query(FIND_ACTIVE_CURRENCY_PAIRS_SQL, CurrencyPairInfoItemRowMapper.map());
    }

    //+
    public CurrencyPair findCurrencyPairByName(String currencyPairName) {
        return jdbcTemplate.queryForObject(
                FIND_CURRENCY_PAIR_BY_NAME_SQL,
                Map.of("currencyPairName", currencyPairName),
                CurrencyPairRowMapper.map());
    }

    //+
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        return jdbcTemplate.queryForObject(
                FIND_CURRENCY_PAIR_BY_ID_SQL,
                Map.of("currencyPairId", currencyPairId),
                CurrencyPairRowMapper.map());
    }

    //+
    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
        return jdbcTemplate.queryForObject(
                FIND_CURRENCY_PAIR_LIMIT_FOR_ROLE_BY_PAIR_AND_TYPE_SQL,
                Map.of(
                        "currency_pair_id", currencyPairId,
                        "user_role_id", roleId,
                        "order_type_id", orderTypeId),
                CurrencyPairLimitRowMapper.map());
    }
}