package me.exrates.service.exception;

/**
 * Created by OLEG on 31.10.2016.
 */
public class IncorrectOrderTypeException extends RuntimeException {
    public IncorrectOrderTypeException() {
    }

    public IncorrectOrderTypeException(String message) {
        super(message);
    }

    public IncorrectOrderTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectOrderTypeException(Throwable cause) {
        super(cause);
    }
}
