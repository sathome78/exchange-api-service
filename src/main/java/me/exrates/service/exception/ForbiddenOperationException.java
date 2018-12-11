package me.exrates.service.exception;

/**
 * Created by OLEG on 18.11.2016.
 */
public class ForbiddenOperationException extends  RuntimeException {
    public ForbiddenOperationException() {
    }

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public ForbiddenOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenOperationException(Throwable cause) {
        super(cause);
    }
}
