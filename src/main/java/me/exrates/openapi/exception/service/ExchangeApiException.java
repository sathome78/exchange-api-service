package me.exrates.openapi.exception.service;

public class ExchangeApiException extends RuntimeException {

    public ExchangeApiException() {
        super();
    }

    public ExchangeApiException(String message) {
        super(message);
    }

    public ExchangeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
