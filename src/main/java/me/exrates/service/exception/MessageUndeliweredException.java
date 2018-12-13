package me.exrates.service.exception;

/**
 * Created by Maks on 29.09.2017.
 */
public class MessageUndeliweredException extends RuntimeException {

    public MessageUndeliweredException() {
    }

    public MessageUndeliweredException(String message) {
        super(message);
    }

    public MessageUndeliweredException(String message, Throwable cause) {
        super(message, cause);
    }
}
