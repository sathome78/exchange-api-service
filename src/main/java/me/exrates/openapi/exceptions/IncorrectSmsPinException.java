package me.exrates.openapi.exceptions;

public class IncorrectSmsPinException extends RuntimeException {

    public IncorrectSmsPinException() {
    }

    public IncorrectSmsPinException(String message) {
        super(message);
    }
}
