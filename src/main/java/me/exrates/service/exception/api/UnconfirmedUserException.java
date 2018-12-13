package me.exrates.service.exception.api;

/**
 * Created by OLEG on 13.10.2016.
 */
public class UnconfirmedUserException extends RuntimeException {
    public UnconfirmedUserException() {
    }

    public UnconfirmedUserException(String message) {
        super(message);
    }

    public UnconfirmedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnconfirmedUserException(Throwable cause) {
        super(cause);
    }
}
