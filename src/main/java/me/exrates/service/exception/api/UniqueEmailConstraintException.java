package me.exrates.service.exception.api;

/**
 * Created by OLEG on 31.08.2016.
 */
public class UniqueEmailConstraintException extends RuntimeException {

    public UniqueEmailConstraintException() {
    }

    public UniqueEmailConstraintException(String message) {
        super(message);
    }

    public UniqueEmailConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueEmailConstraintException(Throwable cause) {
        super(cause);
    }
}
