package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.exrates.openapi.models.dto.CoinmarketApiDto;

import java.math.BigDecimal;

@Data
public class TickerDto {

    private Integer id;
    private String name;
    private BigDecimal last;
    @JsonProperty("lowest_ask")
    private BigDecimal lowestAsk;
    @JsonProperty("highest_bid")
    private BigDecimal highestBid;
    @JsonProperty("percent_change")
    private BigDecimal percentChange;
    @JsonProperty("base_volume")
    private BigDecimal baseVolume;
    @JsonProperty("quote_volume")
    private BigDecimal quoteVolume;
    private BigDecimal high;
    private BigDecimal low;

    public TickerDto(CoinmarketApiDto coinmarketApiDto) {
        this.id = coinmarketApiDto.getCurrencyPairId();
        this.name = coinmarketApiDto.getCurrencyPairName().replace('/', '_');
        this.last = coinmarketApiDto.getLast();
        this.lowestAsk = coinmarketApiDto.getLowestAsk();
        this.highestBid = coinmarketApiDto.getHighestBid();
        this.percentChange = coinmarketApiDto.getPercentChange();
        this.baseVolume = coinmarketApiDto.getBaseVolume();
        this.quoteVolume = coinmarketApiDto.getQuoteVolume();
        this.high = coinmarketApiDto.getHigh24hr();
        this.low = coinmarketApiDto.getLow24hr();
    }
}
