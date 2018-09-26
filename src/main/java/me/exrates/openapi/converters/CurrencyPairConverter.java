package me.exrates.openapi.converters;

public final class CurrencyPairConverter {

    private static final String DELIMITER = "/";

    public static String convert(String currency1, String currency2) {
        final String pairName = currency1 + DELIMITER + currency2;
        return pairName.toUpperCase();
    }
}
