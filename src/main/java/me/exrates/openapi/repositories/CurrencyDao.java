package me.exrates.openapi.repositories;

import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.repositories.mappers.CurrencyPairInfoItemRowMapper;
import me.exrates.openapi.repositories.mappers.CurrencyPairLimitRowMapper;
import me.exrates.openapi.repositories.mappers.CurrencyPairRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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

    private static final String FIND_CURRENCY_PAIR_BY_ORDER_ID_SQL = "SELECT cp.id, cp.currency1_id, cp.currency2_id, cp.name, cp.type, cp.market, " +
            "(SELECT c.name from CURRENCY c where c.id = cp.currency1_id) as currency1_name, " +
            "(SELECT c.name from CURRENCY c where c.id = cp.currency2_id) as currency2_name " +
            " FROM EXORDERS o" +
            " JOIN CURRENCY_PAIR cp ON cp.id = o.currency_pair_id" +
            " WHERE o.id = :order_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return jdbcTemplate.query(FIND_ACTIVE_CURRENCY_PAIRS_SQL, CurrencyPairInfoItemRowMapper.map());
    }

    public CurrencyPair findCurrencyPairByName(String pairName) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_CURRENCY_PAIR_BY_NAME_SQL,
                    Map.of("currencyPairName", pairName),
                    CurrencyPairRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Currency with pairName = %s do not present", pairName));
        }
    }

    public CurrencyPair findCurrencyPairById(int pairId) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_CURRENCY_PAIR_BY_ID_SQL,
                    Map.of("currencyPairId", pairId),
                    CurrencyPairRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Currency with pairId = %d do not present", pairId));
        }
    }

    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer pairId, Integer roleId, Integer orderTypeId) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_CURRENCY_PAIR_LIMIT_FOR_ROLE_BY_PAIR_AND_TYPE_SQL,
                    Map.of(
                            "currency_pair_id", pairId,
                            "user_role_id", roleId,
                            "order_type_id", orderTypeId),
                    CurrencyPairLimitRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Currency with pairId = %d, roleId = %s, orderTypeId = %d do not present", pairId, roleId, orderTypeId));
        }
    }

    public CurrencyPair findCurrencyPairByOrderId(int orderId) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_CURRENCY_PAIR_BY_ORDER_ID_SQL,
                    Map.of("order_id", orderId),
                    CurrencyPairRowMapper.map());
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Currency with orderId = %d do not present", orderId));
        }
    }

    public Integer findActiveCurrencyPairIdByName(String pairName) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_ACTIVE_CURRENCY_PAIR_ID_BY_NAME_SQL,
                    Map.of("pair_name", pairName),
                    Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException(String.format("Active currency with pairName = %s do not present", pairName));
        }
    }
}