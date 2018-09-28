package me.exrates.openapi.exceptions;

public class WrongLimitException extends RuntimeException {

    public WrongLimitException(String message) {
        super(message);
    }
}
