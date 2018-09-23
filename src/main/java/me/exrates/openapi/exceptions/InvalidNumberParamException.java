package me.exrates.openapi.exceptions;

public class InvalidNumberParamException extends RuntimeException {

    public InvalidNumberParamException() {
    }

    public InvalidNumberParamException(String message) {
        super(message);
    }

    public InvalidNumberParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNumberParamException(Throwable cause) {
        super(cause);
    }
}
