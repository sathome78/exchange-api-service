package me.exrates.model.dto.mobileApiDto.dashboard;

import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Valk
 */
public class ExOrderStatisticsShortByPairsApiDto {
    private String currencyPairName;
    private BigDecimal lastOrderRate;
    private BigDecimal predLastOrderRate;

    public ExOrderStatisticsShortByPairsApiDto(ExOrderStatisticsShortByPairsDto dto, Locale locale) {
        this.currencyPairName = dto.getCurrencyPairName();
        this.lastOrderRate = BigDecimalProcessing.parseLocale(dto.getLastOrderRate(), locale, true);
        this.predLastOrderRate = BigDecimalProcessing.parseLocale(dto.getPredLastOrderRate(), locale, true);
    }

    /*hash*/
    @Override
    public int hashCode() {
        int result = currencyPairName != null ? currencyPairName.hashCode() : 0;
        result = 31 * result + (lastOrderRate != null ? lastOrderRate.hashCode() : 0);
        result = 31 * result + (predLastOrderRate != null ? predLastOrderRate.hashCode() : 0);
        return result;
    }
/*getters setters*/
    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public BigDecimal getLastOrderRate() {
        return lastOrderRate;
    }

    public void setLastOrderRate(BigDecimal lastOrderRate) {
        this.lastOrderRate = lastOrderRate;
    }

    public BigDecimal getPredLastOrderRate() {
        return predLastOrderRate;
    }

    public void setPredLastOrderRate(BigDecimal predLastOrderRate) {
        this.predLastOrderRate = predLastOrderRate;
    }
}
