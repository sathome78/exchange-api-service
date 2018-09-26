package me.exrates.openapi.models.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exrates.openapi.models.enums.IntervalType2;

@Getter
@AllArgsConstructor
@ToString
public class ChartTimeFrame {
    private final ChartResolution resolution;
    private final int timeValue;
    private final IntervalType2 timeUnit;

    public String getShortName() {
        return String.join("", String.valueOf(timeValue), timeUnit.getShortName().toLowerCase());
    }
}
