package me.exrates.openapi.model.exceptions;

public class UnsupportedTransactionSourceTypeNameException extends RuntimeException {

    public UnsupportedTransactionSourceTypeNameException(String message) {
        super(message);
    }
}