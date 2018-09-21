package me.exrates.openapi.model.exceptions;

public class UnsupportedTransactionSourceTypeIdException extends RuntimeException {

    public UnsupportedTransactionSourceTypeIdException(String message) {
        super(message);
    }
}