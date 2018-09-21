package me.exrates.openapi.exceptions;

public class LiskCreateAddressException extends RuntimeException {
    public LiskCreateAddressException() {
    }

    public LiskCreateAddressException(String message) {
        super(message);
    }

    public LiskCreateAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiskCreateAddressException(Throwable cause) {
        super(cause);
    }
}
