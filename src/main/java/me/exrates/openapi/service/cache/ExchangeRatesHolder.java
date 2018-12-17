package me.exrates.openapi.service.cache;

import me.exrates.openapi.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;

import java.util.List;

public interface ExchangeRatesHolder {

    List<ExOrderStatisticsShortByPairsDto> getAllRates();

    List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id);
}
