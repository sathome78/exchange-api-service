package me.exrates.service.exception;

public class UnknownAssetIdException extends RuntimeException {

    public UnknownAssetIdException() {
    }

    public UnknownAssetIdException(String message) {
        super(message);
    }
}
