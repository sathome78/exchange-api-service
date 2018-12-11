package me.exrates.service.exception;

/**
 * Created by OLEG on 18.01.2017.
 */
public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException() {
    }

    public WalletNotFoundException(String message) {
        super(message);
    }

    public WalletNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletNotFoundException(Throwable cause) {
        super(cause);
    }
}
