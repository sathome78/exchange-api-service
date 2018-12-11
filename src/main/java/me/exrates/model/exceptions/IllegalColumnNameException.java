package me.exrates.model.exceptions;

/**
 * Created by OLEG on 22.02.2017.
 */
public class IllegalColumnNameException extends RuntimeException {

    public IllegalColumnNameException() {
    }

    public IllegalColumnNameException(String message) {
        super(message);
    }

    public IllegalColumnNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalColumnNameException(Throwable cause) {
        super(cause);
    }
}
