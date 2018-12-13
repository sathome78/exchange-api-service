package me.exrates.service.exception.api;

/**
 * Created by OLEG on 11.10.2016.
 */
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
