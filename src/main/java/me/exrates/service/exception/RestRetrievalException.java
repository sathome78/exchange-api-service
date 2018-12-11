package me.exrates.service.exception;

/**
 * Created by OLEG on 14.12.2016.
 */
public class RestRetrievalException extends RuntimeException {

    public RestRetrievalException() {
    }

    public RestRetrievalException(String message) {
        super(message);
    }

    public RestRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestRetrievalException(Throwable cause) {
        super(cause);
    }
}
