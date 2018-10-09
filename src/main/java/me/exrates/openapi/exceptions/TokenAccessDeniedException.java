package me.exrates.openapi.exceptions;

public class TokenAccessDeniedException extends RuntimeException {
    public TokenAccessDeniedException() {
    }

    public TokenAccessDeniedException(String message) {
        super(message);
    }

    public TokenAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenAccessDeniedException(Throwable cause) {
        super(cause);
    }
}
