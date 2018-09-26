package me.exrates.openapi.repositories.callbacks;

import com.google.common.collect.Lists;
import me.exrates.openapi.models.dto.CandleChartItemDto;
import me.exrates.openapi.models.dto.CoinmarketApiDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.ResultSet;
import java.util.List;

public class StoredProcedureCallback {

    public static PreparedStatementCallback<List<CoinmarketApiDto>> getCoinmarketDataCallback() {
        return ps -> {
            ResultSet rs = ps.executeQuery();
            List<CoinmarketApiDto> list = Lists.newArrayList();
            while (rs.next()) {
                CoinmarketApiDto coinmarketApiDto = CoinmarketApiDto.builder()
                        .currencyPairId(rs.getInt("currency_pair_id"))
                        .currencyPairName(rs.getString("currency_pair_name"))
                        .first(rs.getBigDecimal("first"))
                        .last(rs.getBigDecimal("last"))
                        .lowestAsk(rs.getBigDecimal("lowestAsk"))
                        .highestBid(rs.getBigDecimal("highestBid"))
                        .percentChange(BigDecimalProcessingUtil.doAction(rs.getBigDecimal("first"), rs.getBigDecimal("last"), ActionType.PERCENT_GROWTH))
                        .baseVolume(rs.getBigDecimal("baseVolume"))
                        .quoteVolume(rs.getBigDecimal("quoteVolume"))
                        .isFrozen(rs.getInt("isFrozen"))
                        .high24hr(rs.getBigDecimal("high24hr"))
                        .low24hr(rs.getBigDecimal("low24hr"))
                        .build();
                list.add(coinmarketApiDto);
            }
            rs.close();
            return list;
        };
    }

    public static PreparedStatementCallback<List<CandleChartItemDto>> getDataForCandleChartCallback() {
        return ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = Lists.newArrayList();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = CandleChartItemDto.builder()
                        .beginDate(rs.getTimestamp("pred_point"))
                        .beginPeriod(rs.getTimestamp("pred_point").toLocalDateTime())
                        .endDate(rs.getTimestamp("current_point"))
                        .endPeriod(rs.getTimestamp("current_point").toLocalDateTime())
                        .openRate(rs.getBigDecimal("open_rate"))
                        .closeRate(rs.getBigDecimal("close_rate"))
                        .lowRate(rs.getBigDecimal("low_rate"))
                        .highRate(rs.getBigDecimal("high_rate"))
                        .baseVolume(rs.getBigDecimal("base_volume"))
                        .build();
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        };
    }
}
