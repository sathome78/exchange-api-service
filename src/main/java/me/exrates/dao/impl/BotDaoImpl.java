package me.exrates.dao.impl;

import me.exrates.dao.BotDao;
import me.exrates.model.BotLaunchSettings;
import me.exrates.model.BotTrader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BotDaoImpl implements BotDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<BotTrader> botRowMapper = (rs, rowNum) -> {
        BotTrader botTrader = new BotTrader();
        botTrader.setId(rs.getInt("id"));
        botTrader.setUserId(rs.getInt("user_id"));
        botTrader.setEnabled(rs.getBoolean("is_enabled"));
        botTrader.setAcceptDelayInMillis(rs.getInt("order_accept_timeout"));
        return botTrader;
    };

    private final RowMapper<BotLaunchSettings> botLaunchSettingsRowMapper = (rs, rowNum) -> {
        BotLaunchSettings launchSettings = new BotLaunchSettings();
        launchSettings.setId(rs.getInt("launch_id"));
        launchSettings.setBotId(rs.getInt("bot_trader_id"));
        launchSettings.setCurrencyPairId(rs.getInt("currency_pair_id"));
        launchSettings.setCurrencyPairName(rs.getString("currency_pair_name"));
        launchSettings.setEnabledForPair(rs.getBoolean("is_enabled"));
        launchSettings.setUserOrderPriceConsidered(rs.getBoolean("consider_user_orders"));
        launchSettings.setLaunchIntervalInMinutes(rs.getInt("launch_interval_minutes"));
        launchSettings.setCreateTimeoutInSeconds(rs.getInt("create_timeout_seconds"));
        launchSettings.setQuantityPerSequence(rs.getInt("quantity_per_sequence"));
        return launchSettings;
    };


}
