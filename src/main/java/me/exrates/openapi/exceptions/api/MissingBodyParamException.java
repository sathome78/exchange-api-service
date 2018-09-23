package me.exrates.openapi.exceptions.api;

public class MissingBodyParamException extends RuntimeException {

    public MissingBodyParamException() {
    }

    public MissingBodyParamException(String message) {
        super(message);
    }

    public MissingBodyParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingBodyParamException(Throwable cause) {
        super(cause);
    }
}
