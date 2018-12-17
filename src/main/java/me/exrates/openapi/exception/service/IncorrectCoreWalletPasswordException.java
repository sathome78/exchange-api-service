package me.exrates.openapi.exception.service;

public class IncorrectCoreWalletPasswordException extends RuntimeException {
    public IncorrectCoreWalletPasswordException() {
    }

    public IncorrectCoreWalletPasswordException(String message) {
        super(message);
    }
}
