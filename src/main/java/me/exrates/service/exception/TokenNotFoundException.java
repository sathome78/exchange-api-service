package me.exrates.service.exception;

/**
 * Created by OLEG on 09.09.2016.
 */
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
