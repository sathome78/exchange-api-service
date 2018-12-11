package me.exrates.model.dto.mobileApiDto.dashboard;

import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Valk on 12.04.16.
 */
public class ExOrderStatisticsApiDto {
    private BigDecimal firstOrderAmountBase;
    private BigDecimal firstOrderRate;
    private BigDecimal lastOrderAmountBase;
    private BigDecimal lastOrderRate;
    private BigDecimal minRate;
    private BigDecimal maxRate;
    private BigDecimal sumBase;
    private BigDecimal sumConvert;

    /*constructors*/

    public ExOrderStatisticsApiDto(ExOrderStatisticsDto dto, Locale locale) {
        this.firstOrderAmountBase = BigDecimalProcessing.parseLocale(dto.getFirstOrderAmountBase(), locale, true);
        this.firstOrderRate = BigDecimalProcessing.parseLocale(dto.getFirstOrderRate(), locale, true);
        this.lastOrderAmountBase = BigDecimalProcessing.parseLocale(dto.getLastOrderAmountBase(), locale, true);
        this.lastOrderRate = BigDecimalProcessing.parseLocale(dto.getLastOrderRate(), locale, true);
        this.minRate = BigDecimalProcessing.parseLocale(dto.getMinRate(), locale, true);
        this.maxRate = BigDecimalProcessing.parseLocale(dto.getMaxRate(), locale, true);
        this.sumBase = BigDecimalProcessing.parseLocale(dto.getSumBase(), locale, true);
        this.sumConvert = BigDecimalProcessing.parseLocale(dto.getSumConvert(), locale, true);
    }

    public BigDecimal getFirstOrderAmountBase() {
        return firstOrderAmountBase;
    }

    public void setFirstOrderAmountBase(BigDecimal firstOrderAmountBase) {
        this.firstOrderAmountBase = firstOrderAmountBase;
    }

    public BigDecimal getFirstOrderRate() {
        return firstOrderRate;
    }

    public void setFirstOrderRate(BigDecimal firstOrderRate) {
        this.firstOrderRate = firstOrderRate;
    }

    public BigDecimal getLastOrderAmountBase() {
        return lastOrderAmountBase;
    }

    public void setLastOrderAmountBase(BigDecimal lastOrderAmountBase) {
        this.lastOrderAmountBase = lastOrderAmountBase;
    }

    public BigDecimal getLastOrderRate() {
        return lastOrderRate;
    }

    public void setLastOrderRate(BigDecimal lastOrderRate) {
        this.lastOrderRate = lastOrderRate;
    }

    public BigDecimal getMinRate() {
        return minRate;
    }

    public void setMinRate(BigDecimal minRate) {
        this.minRate = minRate;
    }

    public BigDecimal getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(BigDecimal maxRate) {
        this.maxRate = maxRate;
    }

    public BigDecimal getSumBase() {
        return sumBase;
    }

    public void setSumBase(BigDecimal sumBase) {
        this.sumBase = sumBase;
    }

    public BigDecimal getSumConvert() {
        return sumConvert;
    }

    public void setSumConvert(BigDecimal sumConvert) {
        this.sumConvert = sumConvert;
    }
}
