package me.exrates.openapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CandleChartItemDto {

    private LocalDateTime beginPeriod;
    private LocalDateTime endPeriod;
    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;
    private Timestamp beginDate;
    private Timestamp endDate;
}
