package me.exrates.model.exceptions;

public class UnsupportedTransactionSourceTypeNameException extends RuntimeException {

    public UnsupportedTransactionSourceTypeNameException(String message) {
        super(message);
    }
}