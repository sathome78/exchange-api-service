package me.exrates.service.exception;

public class InvalidBtcPaymentDataException extends RuntimeException {
    public InvalidBtcPaymentDataException() {
    }

    public InvalidBtcPaymentDataException(String message) {
        super(message);
    }
}
