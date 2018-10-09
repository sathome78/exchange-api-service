package me.exrates.openapi.exceptions;

public class IncorrectCurrentUserException extends RuntimeException {

    public IncorrectCurrentUserException(String message) {
        super(message);
    }
}
