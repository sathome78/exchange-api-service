package me.exrates.model.exceptions;

public class HmacSignatureBuildException extends RuntimeException {
    public HmacSignatureBuildException() {
    }

    public HmacSignatureBuildException(String message) {
        super(message);
    }

    public HmacSignatureBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public HmacSignatureBuildException(Throwable cause) {
        super(cause);
    }
}
