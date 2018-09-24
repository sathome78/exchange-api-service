package me.exrates.openapi.utils;

import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OpenApiUtils {

    private static final Predicate<String> currencyPairNamePattern = Pattern.compile("^[a-z0-9]{2,8}_[a-z0-9]{2,8}$").asPredicate();

    //+
    public static String formatCurrencyPairNameParam(String currencyPair) {
        if (!currencyPairNamePattern.test(currencyPair)) {
            throw new InvalidCurrencyPairFormatException(currencyPair);
        }
        return currencyPair.replace('_', '/').toUpperCase();
    }
}
