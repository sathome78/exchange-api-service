package me.exrates.openapi.exception.service;

public class CoreWalletPasswordNotFoundException extends RuntimeException {
    public CoreWalletPasswordNotFoundException() {
    }

    public CoreWalletPasswordNotFoundException(String message) {
        super(message);
    }
}
