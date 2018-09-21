package me.exrates.openapi.model.dto;


import lombok.Getter;
import lombok.Setter;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class UserActivitiesInPeriodDto {
    private Integer refillNum;
    private String userEmail;
    private String startDate;
    private String lastDate;
    private BigDecimal entries;

    public static String getTitle() {
        return Stream.of("No.", "user_email", "start_date", "last_date", "entries")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(
                String.valueOf(refillNum),
                userEmail,
                startDate,
                lastDate,
                BigDecimalProcessing.formatNoneComma(entries, false)
        ).collect(Collectors.joining(";", "", "\r\n"));
    }
}