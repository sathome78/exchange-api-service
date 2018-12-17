package me.exrates.openapi.exception.service;

public class AuthenticationNotAvailableException extends RuntimeException {
    public AuthenticationNotAvailableException() {
    }

    public AuthenticationNotAvailableException(String message) {
        super(message);
    }

    public AuthenticationNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationNotAvailableException(Throwable cause) {
        super(cause);
    }
}
