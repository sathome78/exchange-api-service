package me.exrates.openapi.exception.model;

public class UnsupportedTransactionSourceTypeIdException extends RuntimeException {

    public UnsupportedTransactionSourceTypeIdException(String message) {
        super(message);
    }
}