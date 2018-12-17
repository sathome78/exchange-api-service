package me.exrates.openapi.exception.model;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
