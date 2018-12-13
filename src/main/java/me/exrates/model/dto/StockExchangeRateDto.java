package me.exrates.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import me.exrates.model.StockExchangeStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLEG on 15.12.2016.
 */
public class StockExchangeRateDto {
    @JsonProperty(value = "currencyPair")
    private String currencyPairName;
    @JsonProperty(value = "exchangeStats")
    private List<StockExchangeStats> exchangeStats = new ArrayList<>();

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public List<StockExchangeStats> getExchangeStats() {
        return exchangeStats;
    }

    public void setExchangeStats(List<StockExchangeStats> exchangeStats) {
        this.exchangeStats = exchangeStats;
    }

    @Override
    public String toString() {
        return "StockExchangeRateDto{" +
                "currencyPairName='" + currencyPairName + '\'' +
                ", exchangeStats=" + exchangeStats +
                '}';
    }
}
