package me.exrates.model.dto.mobileApiDto;

import lombok.Data;
import me.exrates.model.dto.CandleChartItemDto;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CandleChartItemReducedDto {

    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;
    private Timestamp beginDate;
    private Timestamp endDate;

    public CandleChartItemReducedDto(CandleChartItemDto candleChartItemDto) {
        this.openRate = candleChartItemDto.getOpenRate();
        this.closeRate = candleChartItemDto.getCloseRate();
        this.lowRate = candleChartItemDto.getLowRate();
        this.highRate = candleChartItemDto.getHighRate();
        this.baseVolume = candleChartItemDto.getBaseVolume();
        this.beginDate = candleChartItemDto.getBeginDate();
        this.endDate = candleChartItemDto.getEndDate();
    }
}
