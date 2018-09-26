package me.exrates.openapi.models.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CandleChartItemReducedDto {

    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;

    public CandleChartItemReducedDto(CandleChartItemDto candleChartItemDto) {
        this.openRate = candleChartItemDto.getOpenRate();
        this.closeRate = candleChartItemDto.getCloseRate();
        this.lowRate = candleChartItemDto.getLowRate();
        this.highRate = candleChartItemDto.getHighRate();
        this.baseVolume = candleChartItemDto.getBaseVolume();
        this.beginDate = candleChartItemDto.getBeginDate().toLocalDateTime();
        this.endDate = candleChartItemDto.getEndDate().toLocalDateTime();
    }
}
