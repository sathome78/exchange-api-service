package me.exrates.openapi.exceptions;

public class UnknownAssetIdException extends RuntimeException {

    public UnknownAssetIdException() {
    }

    public UnknownAssetIdException(String message) {
        super(message);
    }
}
