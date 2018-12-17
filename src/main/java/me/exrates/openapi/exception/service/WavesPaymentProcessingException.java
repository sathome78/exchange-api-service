package me.exrates.openapi.exception.service;

public class WavesPaymentProcessingException extends RuntimeException {

    public WavesPaymentProcessingException() {
    }

    public WavesPaymentProcessingException(String message) {
        super(message);
    }

    public WavesPaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavesPaymentProcessingException(Throwable cause) {
        super(cause);
    }
}
