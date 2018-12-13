package me.exrates.model;

import com.fasterxml.jackson.databind.JsonNode;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
public class StockExchange {

    private Integer id;
    private String name;
    private String lastFieldName;
    private String buyFieldName;
    private String sellFieldName;
    private String lowFieldName;
    private String highFieldName;
    private String volumeFieldName;
    private List<CurrencyPair> availableCurrencyPairs = new ArrayList<>();
    private Map<String, String> currencyAliases = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastFieldName() {
        return lastFieldName;
    }

    public void setLastFieldName(String lastFieldName) {
        this.lastFieldName = lastFieldName;
    }

    public String getBuyFieldName() {
        return buyFieldName;
    }

    public void setBuyFieldName(String buyFieldName) {
        this.buyFieldName = buyFieldName;
    }

    public String getSellFieldName() {
        return sellFieldName;
    }

    public void setSellFieldName(String sellFieldName) {
        this.sellFieldName = sellFieldName;
    }

    public String getLowFieldName() {
        return lowFieldName;
    }

    public void setLowFieldName(String lowFieldName) {
        this.lowFieldName = lowFieldName;
    }

    public String getHighFieldName() {
        return highFieldName;
    }

    public void setHighFieldName(String highFieldName) {
        this.highFieldName = highFieldName;
    }

    public String getVolumeFieldName() {
        return volumeFieldName;
    }

    public void setVolumeFieldName(String volumeFieldName) {
        this.volumeFieldName = volumeFieldName;
    }

    public List<CurrencyPair> getAvailableCurrencyPairs() {
        return availableCurrencyPairs;
    }

    public void setAvailableCurrencyPairs(List<CurrencyPair> availableCurrencyPairs) {
        this.availableCurrencyPairs = availableCurrencyPairs;
    }

    public Map<String, String> getCurrencyAliases() {
        return currencyAliases;
    }

    public void setCurrencyAliases(Map<String, String> currencyAliases) {
        this.currencyAliases = currencyAliases;
    }

    public Map<String, CurrencyPair> getAliasedCurrencyPairs(BiFunction<String, String, String> transformer) {
       return availableCurrencyPairs.stream().collect(Collectors.toMap(currencyPair -> transformer.apply(
               currencyAliases.getOrDefault(currencyPair.getCurrency1().getName(), currencyPair.getCurrency1().getName()),
               currencyAliases.getOrDefault(currencyPair.getCurrency2().getName(), currencyPair.getCurrency2().getName())),
                currencyPair -> currencyPair));

    }

    public StockExchangeStats extractStatsFromNode(JsonNode jsonNode, int currencyPairId) {
        StockExchangeStats stockExchangeStats = new StockExchangeStats();
        stockExchangeStats.setPriceLast(extractNumberForExistingName(jsonNode, lastFieldName));
        stockExchangeStats.setPriceBuy(extractNumberForExistingName(jsonNode, buyFieldName));
        stockExchangeStats.setPriceSell(extractNumberForExistingName(jsonNode, sellFieldName));
        stockExchangeStats.setPriceLow(extractNumberForExistingName(jsonNode, lowFieldName));
        stockExchangeStats.setPriceHigh(extractNumberForExistingName(jsonNode, highFieldName));
        stockExchangeStats.setVolume(extractNumberForExistingName(jsonNode, volumeFieldName));
        stockExchangeStats.setDate(LocalDateTime.now());
        stockExchangeStats.setStockExchange(this);
        stockExchangeStats.setCurrencyPairId(currencyPairId);
        return stockExchangeStats;
    }

    private BigDecimal extractNumberForExistingName(JsonNode jsonNode, String fieldName) {
        return fieldName == null ? null : BigDecimalProcessing.parseNonePoint(jsonNode.get(fieldName).asText());
    }

    @Override
    public String toString() {
        return "StockExchange{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", availableCurrencyPairs=" + availableCurrencyPairs +
                '}';
    }
}
