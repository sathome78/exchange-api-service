package me.exrates.model.exceptions;

/**
 * Created by OLEG on 09.11.2016.
 */
public class UnsupportedNotificationEventException extends RuntimeException {

    public UnsupportedNotificationEventException() {
    }

    public UnsupportedNotificationEventException(String message) {
        super(message);
    }

    public UnsupportedNotificationEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedNotificationEventException(Throwable cause) {
        super(cause);
    }
}
