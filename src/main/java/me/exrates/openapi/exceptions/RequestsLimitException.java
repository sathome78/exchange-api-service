package me.exrates.openapi.exceptions;

public class RequestsLimitException extends Exception {

    public RequestsLimitException(String message) {
        super(message);
    }
}
