package me.exrates.openapi.exception.model;

public class UnsupportedTransactionSourceTypeNameException extends RuntimeException {

    public UnsupportedTransactionSourceTypeNameException(String message) {
        super(message);
    }
}