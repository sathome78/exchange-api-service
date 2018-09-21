package me.exrates.openapi.model.dto.mobileApiDto;

import me.exrates.model.dto.CandleChartItemDto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by OLEG on 05.09.2016.
 */
public class CandleChartItemReducedDto {
    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;
    private Timestamp beginDate;
    private Timestamp endDate;

    public CandleChartItemReducedDto() {
    }

    public CandleChartItemReducedDto(CandleChartItemDto candleChartItemDto) {
        this.openRate = candleChartItemDto.getOpenRate();
        this.closeRate = candleChartItemDto.getCloseRate();
        this.lowRate = candleChartItemDto.getLowRate();
        this.highRate = candleChartItemDto.getHighRate();
        this.baseVolume = candleChartItemDto.getBaseVolume();
        this.beginDate = candleChartItemDto.getBeginDate();
        this.endDate = candleChartItemDto.getEndDate();
    }

    public BigDecimal getOpenRate() {
        return openRate;
    }

    public void setOpenRate(BigDecimal openRate) {
        this.openRate = openRate;
    }

    public BigDecimal getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(BigDecimal closeRate) {
        this.closeRate = closeRate;
    }

    public BigDecimal getLowRate() {
        return lowRate;
    }

    public void setLowRate(BigDecimal lowRate) {
        this.lowRate = lowRate;
    }

    public BigDecimal getHighRate() {
        return highRate;
    }

    public void setHighRate(BigDecimal highRate) {
        this.highRate = highRate;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "CandleChartItemReducedDto{" +
                "openRate=" + openRate +
                ", closeRate=" + closeRate +
                ", lowRate=" + lowRate +
                ", highRate=" + highRate +
                ", baseVolume=" + baseVolume +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                '}';
    }
}
