package me.exrates.service.exception;

/**
 * Created by OLEG on 09.09.2016.
 */
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
