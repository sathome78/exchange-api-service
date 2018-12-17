package me.exrates.openapi.exception.service;

public class IncorrectCurrentUserException extends RuntimeException {

    public IncorrectCurrentUserException() {
    }

    public IncorrectCurrentUserException(String message) {
        super(message);
    }

    public IncorrectCurrentUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectCurrentUserException(Throwable cause) {
        super(cause);
    }
}
