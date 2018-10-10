package me.exrates.openapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.exceptions.InvalidCurrencyPairFormatException;
import me.exrates.openapi.exceptions.WrongDateOrderException;
import me.exrates.openapi.exceptions.WrongLimitException;

import java.time.LocalDate;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidationUtil {

    private static final Pattern CURRENCY_PAIR_NAME_PATTERN = Pattern.compile("^[A-Z0-9]{2,8}/[A-Z0-9]{2,8}$");

    public static void validateCurrencyPair(String pair) {
        boolean isValid = CURRENCY_PAIR_NAME_PATTERN.matcher(pair).matches();
        if (!isValid) {
            throw new InvalidCurrencyPairFormatException(String.format("Currency pair name %s not valid", pair));
        }
    }

    public static void validateLimit(Integer limit) {
        boolean isValid = nonNull(limit) && limit > 0;
        if (!isValid) {
            throw new WrongLimitException("Limit value equals or less than zero");
        }
    }

    public static void validateDate(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new WrongDateOrderException("From date is after to date");
        }
    }
}
