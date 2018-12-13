package me.exrates.service.exception;

public class WalletsApiException extends RuntimeException {

    public WalletsApiException() {
        super();
    }

    public WalletsApiException(String message) {
        super(message);
    }

    public WalletsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
