package me.exrates.service.exception;

public class NeoPaymentProcessingException extends RuntimeException {

    public NeoPaymentProcessingException() {
    }

    public NeoPaymentProcessingException(String message) {
        super(message);
    }

    public NeoPaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeoPaymentProcessingException(Throwable cause) {
        super(cause);
    }
}
