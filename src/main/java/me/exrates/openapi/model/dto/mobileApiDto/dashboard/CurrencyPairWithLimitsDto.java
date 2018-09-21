package me.exrates.openapi.model.dto.mobileApiDto.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.CurrencyPairType;

import java.math.BigDecimal;

/**
 * Created by OLEG on 10.04.2017.
 */
@Getter @Setter
@ToString
public class CurrencyPairWithLimitsDto {
  private int id;
  private String name;
  private Currency currency1;
  private Currency currency2;
  private BigDecimal minRateSell;
  private BigDecimal maxRateSell;
  private BigDecimal minRateBuy;
  private BigDecimal maxRateBuy;
  private BigDecimal minAmountSell;
  private BigDecimal maxAmountSell;
  private BigDecimal minAmountBuy;
  private BigDecimal maxAmountBuy;
  private CurrencyPairType type;

  public CurrencyPairWithLimitsDto(CurrencyPair currencyPair, BigDecimal minRateSell,
                                   BigDecimal maxRateSell, BigDecimal minRateBuy, BigDecimal maxRateBuy, BigDecimal minAmountSell,
                                   BigDecimal maxAmountSell, BigDecimal minAmountBuy, BigDecimal maxAmountBuy) {
    this.id = currencyPair.getId();
    this.name = currencyPair.getName();
    this.currency1 = currencyPair.getCurrency1();
    this.currency2 = currencyPair.getCurrency2();
    this.minRateSell = minRateSell;
    this.maxRateSell = maxRateSell;
    this.minRateBuy = minRateBuy;
    this.maxRateBuy = maxRateBuy;
    this.minAmountSell = minAmountSell;
    this.maxAmountSell = maxAmountSell;
    this.minAmountBuy = minAmountBuy;
    this.maxAmountBuy = maxAmountBuy;
    this.type = currencyPair.getPairType();
  }
}
