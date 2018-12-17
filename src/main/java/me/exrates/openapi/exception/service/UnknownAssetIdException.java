package me.exrates.openapi.exception.service;

public class UnknownAssetIdException extends RuntimeException {

    public UnknownAssetIdException() {
    }

    public UnknownAssetIdException(String message) {
        super(message);
    }
}
