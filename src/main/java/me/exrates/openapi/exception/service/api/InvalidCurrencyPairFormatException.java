package me.exrates.openapi.exception.service.api;

public class InvalidCurrencyPairFormatException extends RuntimeException {

    public InvalidCurrencyPairFormatException(String message) {
        super(message);
    }

}
