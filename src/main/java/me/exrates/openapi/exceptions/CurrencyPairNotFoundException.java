package me.exrates.openapi.exceptions;

public class CurrencyPairNotFoundException extends RuntimeException {

    public CurrencyPairNotFoundException(String message) {
        super(message);
    }
}
