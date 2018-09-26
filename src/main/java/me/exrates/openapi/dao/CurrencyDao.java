package me.exrates.openapi.dao;

import me.exrates.openapi.dao.mappers.CurrencyPairInfoItemRowMapper;
import me.exrates.openapi.dao.mappers.CurrencyPairRowMapper;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.dto.CurrencyPairLimitDto;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDao {

    private static final String FIND_ACTIVE_CURRENCY_PAIR_BY_NAME_SQL = "SELECT cp.id FROM CURRENCY_PAIR cp WHERE cp.name = :pair_name AND cp.hidden != 1";

    private static final String FIND_ACTIVE_CURRENCY_PAIRS_SQL = "SELECT cp.name FROM CURRENCY_PAIR cp WHERE cp.hidden != 1 ORDER BY cp.name ASC";

    @Autowired
    private NamedParameterJdbcTemplate npJdbcTemplate;

    //+
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type," +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE id = :currencyPairId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
        return npJdbcTemplate.queryForObject(sql, namedParameters, CurrencyPairRowMapper.map());
    }

    //+
    public CurrencyPair findCurrencyPairByName(String currencyPairName) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type," +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE name = :currencyPairName";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairName", String.valueOf(currencyPairName));
        return npJdbcTemplate.queryForObject(sql, namedParameters, CurrencyPairRowMapper.map());
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
        return npJdbcTemplate.queryForObject(sql, namedParameters, CurrencyPairRowMapper.map());
    }

    //+
    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
        String sql = "SELECT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, lim.min_rate, lim.max_rate, " +
                "lim.min_amount, lim.max_amount " +
                " FROM CURRENCY_PAIR_LIMIT lim " +
                " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id AND CURRENCY_PAIR.hidden != 1 " +
                " WHERE lim.currency_pair_id = :currency_pair_id AND lim.user_role_id = :user_role_id AND lim.order_type_id = :order_type_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("user_role_id", roleId);
        namedParameters.put("order_type_id", orderTypeId);
        return npJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
            CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
            dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            dto.setCurrencyPairName(rs.getString("currency_pair_name"));
            dto.setMinRate(rs.getBigDecimal("min_rate"));
            dto.setMaxRate(rs.getBigDecimal("max_rate"));
            dto.setMinAmount(rs.getBigDecimal("min_amount"));
            dto.setMaxAmount(rs.getBigDecimal("max_amount"));
            return dto;
        });
    }

    //+
    public CurrencyPair findActiveCurrencyPairByName(String pairName) {
        Map<String, String> params = Collections.singletonMap("pair_name", pairName);

        return npJdbcTemplate.queryForObject(FIND_ACTIVE_CURRENCY_PAIR_BY_NAME_SQL, params, CurrencyPairRowMapper.map());
    }

    //+
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return npJdbcTemplate.query(FIND_ACTIVE_CURRENCY_PAIRS_SQL, CurrencyPairInfoItemRowMapper.map());
    }
}