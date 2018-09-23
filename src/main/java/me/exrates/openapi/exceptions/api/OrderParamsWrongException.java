package me.exrates.openapi.exceptions.api;

public class OrderParamsWrongException extends RuntimeException {

    public OrderParamsWrongException() {
        super();
    }

    public OrderParamsWrongException(String message) {
        super(message);
    }
}
