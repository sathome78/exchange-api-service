package me.exrates.service.exception.api;

/**
 * Created by OLEG on 07.10.2016.
 */
public class KeyNotFoundException extends RuntimeException {
    public KeyNotFoundException() {
    }

    public KeyNotFoundException(String message) {
        super(message);
    }

    public KeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyNotFoundException(Throwable cause) {
        super(cause);
    }
}
