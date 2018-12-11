package me.exrates.model.exceptions;

public class UnsupportedTransactionSourceTypeIdException extends RuntimeException {

    public UnsupportedTransactionSourceTypeIdException(String message) {
        super(message);
    }
}