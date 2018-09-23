package me.exrates.openapi.exceptions;

public class CurrencyPairNotFoundException extends RuntimeException {

    public CurrencyPairNotFoundException() {
    }

    public CurrencyPairNotFoundException(String message) {
        super(message);
    }

    public CurrencyPairNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyPairNotFoundException(Throwable cause) {
        super(cause);
    }
}
