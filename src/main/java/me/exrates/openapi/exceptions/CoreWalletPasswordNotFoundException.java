package me.exrates.openapi.exceptions;

public class CoreWalletPasswordNotFoundException extends RuntimeException {

    public CoreWalletPasswordNotFoundException() {
    }

    public CoreWalletPasswordNotFoundException(String message) {
        super(message);
    }
}
