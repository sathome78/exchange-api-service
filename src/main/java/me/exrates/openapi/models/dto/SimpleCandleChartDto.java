package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("SimpleCandleChartResponse")
@Data
public class SimpleCandleChartDto {

    @ApiModelProperty(value = "open rate", position = 1, required = true)
    @JsonProperty("open_rate")
    private BigDecimal openRate;
    @ApiModelProperty(value = "close rate", position = 2, required = true)
    @JsonProperty("close_rate")
    private BigDecimal closeRate;
    @ApiModelProperty(value = "low rate", position = 3, required = true)
    @JsonProperty("low_rate")
    private BigDecimal lowRate;
    @ApiModelProperty(value = "high rate", position = 4, required = true)
    @JsonProperty("high_rate")
    private BigDecimal highRate;
    @ApiModelProperty(value = "base volume", position = 5, required = true)
    @JsonProperty("base_volume")
    private BigDecimal baseVolume;
    @ApiModelProperty(value = "begin date", position = 6, required = true)
    @JsonProperty("begin_date")
    private LocalDateTime beginDate;
    @ApiModelProperty(value = "end date", position = 7, required = true)
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    public SimpleCandleChartDto(CandleChartItemDto candleChartItemDto) {
        this.openRate = candleChartItemDto.getOpenRate();
        this.closeRate = candleChartItemDto.getCloseRate();
        this.lowRate = candleChartItemDto.getLowRate();
        this.highRate = candleChartItemDto.getHighRate();
        this.baseVolume = candleChartItemDto.getBaseVolume();
        this.beginDate = candleChartItemDto.getBeginDate().toLocalDateTime();
        this.endDate = candleChartItemDto.getEndDate().toLocalDateTime();
    }
}
