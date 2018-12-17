package me.exrates.openapi.exception.service;

public class InvalidBtcPaymentDataException extends RuntimeException {
    public InvalidBtcPaymentDataException() {
    }

    public InvalidBtcPaymentDataException(String message) {
        super(message);
    }
}
