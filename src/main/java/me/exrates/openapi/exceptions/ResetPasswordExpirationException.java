package me.exrates.openapi.exceptions;

public class ResetPasswordExpirationException extends RuntimeException {

    public ResetPasswordExpirationException() {
    }

    public ResetPasswordExpirationException(String message) {
        super(message);
    }

    public ResetPasswordExpirationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResetPasswordExpirationException(Throwable cause) {
        super(cause);
    }
}
