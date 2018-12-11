package me.exrates.utils;

import me.exrates.controller.exception.InvalidNumberParamException;

import static java.util.Objects.nonNull;

public final class ValidationUtil {

    public static void validateNaturalInt(Integer number) {
        if (nonNull(number) && number <= 0) {
            throw new InvalidNumberParamException(String.format("Number shouldn't be equals to zero or be negative: %s", number));
        }
    }
}
