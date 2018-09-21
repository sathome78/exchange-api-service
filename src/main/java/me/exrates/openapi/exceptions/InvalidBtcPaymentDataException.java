package me.exrates.openapi.exceptions;

public class InvalidBtcPaymentDataException extends RuntimeException {
    public InvalidBtcPaymentDataException() {
    }

    public InvalidBtcPaymentDataException(String message) {
        super(message);
    }
}
