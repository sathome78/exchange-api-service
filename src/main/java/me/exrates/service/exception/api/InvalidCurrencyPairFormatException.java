package me.exrates.service.exception.api;

public class InvalidCurrencyPairFormatException extends RuntimeException {

    public InvalidCurrencyPairFormatException(String message) {
        super(message);
    }

}
