package me.exrates.openapi.models.enums;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum IntervalType2 {

    HOUR(ChronoUnit.HOURS, 3L, true, "H"),
    DAY(ChronoUnit.DAYS, 5L, true, "D"),
    WEAK(ChronoUnit.WEEKS, 20L, true, "W"),
    YEAR(ChronoUnit.YEARS, 86400L, true, "Y"),
    MONTH(ChronoUnit.MONTHS, 3600L, true, "M");

    private TemporalUnit correspondingTimeUnit;

    private Long chartRefreshInterval;

    private boolean chartLazyUpdate;

    private String shortName;

    IntervalType2(TemporalUnit correspondingTimeUnit, Long chartRefreshInterval, boolean chartLazyUpdate, String shortName) {
        this.correspondingTimeUnit = correspondingTimeUnit;
        this.chartRefreshInterval = chartRefreshInterval;
        this.chartLazyUpdate = chartLazyUpdate;
        this.shortName = shortName;
    }

    public TemporalUnit getCorrespondingTimeUnit() {
        return correspondingTimeUnit;
    }

    public String getShortName() {
        return shortName;
    }
}
