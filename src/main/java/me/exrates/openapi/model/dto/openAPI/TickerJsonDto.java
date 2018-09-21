package me.exrates.openapi.model.dto.openAPI;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

/**
 * Created by OLEG on 20.03.2017.
 */
@Getter @Setter
@EqualsAndHashCode
@ToString
public class TickerJsonDto {
  private Integer id;
  private String name;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal last;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal lowestAsk;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal highestBid;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal percentChange;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal baseVolume;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal quoteVolume;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal high;
  @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
  private BigDecimal low;

  public TickerJsonDto(CoinmarketApiDto coinmarketApiDto) {
    this.id = coinmarketApiDto.getCurrencyPairId();
    this.name = coinmarketApiDto.getCurrency_pair_name().replace('/', '_');
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
