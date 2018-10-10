package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("TickerItemResponse")
@Data
public class TickerItemDto {

    @ApiModelProperty(value = "ticker id", position = 1, required = true)
    private Integer id;
    @ApiModelProperty(value = "ticker name", position = 2, required = true)
    private String name;
    @ApiModelProperty(value = "last accepted price", position = 3, required = true)
    private BigDecimal last;
    @ApiModelProperty(value = "lowest ask", position = 4, required = true)
    @JsonProperty("lowest_ask")
    private BigDecimal lowestAsk;
    @ApiModelProperty(value = "highest bid", position = 5, required = true)
    @JsonProperty("highest_bid")
    private BigDecimal highestBid;
    @ApiModelProperty(value = "percent change", position = 6, required = true)
    @JsonProperty("percent_change")
    private BigDecimal percentChange;
    @ApiModelProperty(value = "base volume (1st currency amount)", position = 7, required = true)
    @JsonProperty("base_volume")
    private BigDecimal baseVolume;
    @ApiModelProperty(value = "quote volume (2nd currency amount)", position = 8, required = true)
    @JsonProperty("quote_volume")
    private BigDecimal quoteVolume;
    @ApiModelProperty(value = "highest price", position = 9, required = true)
    private BigDecimal high;
    @ApiModelProperty(value = "lowest price", position = 10, required = true)
    private BigDecimal low;

    public TickerItemDto(CoinmarketApiDto coinmarketApiDto) {
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
