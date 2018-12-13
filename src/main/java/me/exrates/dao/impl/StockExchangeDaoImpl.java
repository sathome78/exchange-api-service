package me.exrates.dao.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
@Repository
public class StockExchangeDaoImpl implements StockExchangeDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_STOCK_EXCHANGE = "SELECT SE.id AS stock_exchange_id, SE.name AS stock_exchange_name, " +
            "SE.last_field_name, SE.buy_field_name, SE.sell_field_name, SE.high_field_name, SE.low_field_name, SE.volume_field_name, " +
            "CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, CURRENCY_PAIR.name, CURRENCY_PAIR.type, CURRENCY_PAIR.market, " +
            "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
            "(select name from CURRENCY where id = currency2_id) as currency2_name," +
            " cur1_alias.alias AS currency1_alias, cur2_alias.alias AS currency2_alias " +
            " FROM STOCK_EXCHANGE SE " +
            "INNER JOIN STOCK_CURRENCY_PAIR SCP ON SCP.stock_exchange_id = SE.id " +
            "INNER JOIN CURRENCY_PAIR ON SCP.currency_pair_id = CURRENCY_PAIR.id " +
            "LEFT JOIN STOCK_EXCHANGE_CURRENCY_ALIAS cur1_alias ON SE.id = cur1_alias.stock_exchange_id " +
            "AND CURRENCY_PAIR.currency1_id = cur1_alias.currency_id " +
            "LEFT JOIN STOCK_EXCHANGE_CURRENCY_ALIAS cur2_alias ON SE.id = cur2_alias.stock_exchange_id " +
            "AND CURRENCY_PAIR.currency2_id = cur2_alias.currency_id ";

    private final String CREATE_STOCK_EXRATE = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, price_last, " +
            " price_buy, price_sell, price_low, price_high, volume) " +
            "VALUES(:currency_pair_id, :stock_exchange_id, :price_last, :price_buy, :price_sell, :price_low, :price_high, :volume)";

    private final ResultSetExtractor<List<StockExchange>> stockExchangeResultSetExtractor = (resultSet -> {
        List<StockExchange> result = new ArrayList<>();
        StockExchange stockExchange = null;
        int lastStockExchangeId = 0;
        while (resultSet.next()) {
            int currentStockExchangeId = resultSet.getInt("stock_exchange_id");
            if (currentStockExchangeId != lastStockExchangeId) {
                lastStockExchangeId = currentStockExchangeId;
                stockExchange = new StockExchange();
                result.add(stockExchange);
                stockExchange.setId(currentStockExchangeId);
                stockExchange.setName(resultSet.getString("stock_exchange_name"));
                stockExchange.setLastFieldName(resultSet.getString("last_field_name"));
                stockExchange.setBuyFieldName(resultSet.getString("buy_field_name"));
                stockExchange.setSellFieldName(resultSet.getString("sell_field_name"));
                stockExchange.setLowFieldName(resultSet.getString("low_field_name"));
                stockExchange.setHighFieldName(resultSet.getString("high_field_name"));
                stockExchange.setVolumeFieldName(resultSet.getString("volume_field_name"));
            }
            CurrencyPair currencyPair = CurrencyDaoImpl.currencyPairRowMapper.mapRow(resultSet, resultSet.getRow());
            String currency1Alias = resultSet.getString("currency1_alias");
            String currency2Alias = resultSet.getString("currency2_alias");
            if (stockExchange != null) {
                stockExchange.getAvailableCurrencyPairs().add(currencyPair);
                if (currency1Alias != null) {
                    stockExchange.getCurrencyAliases().put(resultSet.getString("currency1_name"), currency1Alias);
                }
                if (currency2Alias != null) {
                    stockExchange.getCurrencyAliases().put(resultSet.getString("currency2_name"), currency2Alias);
                }

            }
        }
        return result;
    });


}
