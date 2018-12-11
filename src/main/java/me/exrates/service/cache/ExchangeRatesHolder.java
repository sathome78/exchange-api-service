package me.exrates.service.cache;

import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRatesHolder {

    List<ExOrderStatisticsShortByPairsDto> getAllRates();

    List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id);
}
