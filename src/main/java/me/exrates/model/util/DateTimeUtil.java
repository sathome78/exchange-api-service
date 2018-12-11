package me.exrates.model.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
public class DateTimeUtil {
  public static LocalDateTime stringToLocalDateTime(String dateTime) {
    return dateTime == null ? null : LocalDateTime.parse(dateTime.replace(" ", "T"));
  }

  public static LocalDate stringToLocalDate(String date) {
    return date == null ? null : LocalDate.parse(date);
  }
}
