package me.exrates.openapi.model.enums;

import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

/**
 * Created by Valk on 27.04.2016.
 */
public enum IntervalType {
    MINUTE(ChronoUnit.MINUTES),
    HOUR(ChronoUnit.HOURS),
    DAY(ChronoUnit.DAYS),
    YEAR(ChronoUnit.YEARS),
    MONTH(ChronoUnit.MONTHS);
    
    private TemporalUnit correspondingTimeUnit;

    IntervalType(TemporalUnit correspondingTimeUnit) {
        this.correspondingTimeUnit = correspondingTimeUnit;
    }
    
    public TemporalUnit getCorrespondingTimeUnit() {
        return correspondingTimeUnit;
    }

    public static IntervalType convert(String str) {
        return Arrays.stream(IntervalType.values()).filter(val -> val.name().equals(str))
                .findFirst().orElseThrow(() -> new UnsupportedIntervalTypeException(str));
    }
}
