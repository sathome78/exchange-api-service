package me.exrates.openapi.exception.service;

/**
 * Created by maks on 20.07.2017.
 */
public class NisNotReadyException extends RuntimeException {

    public NisNotReadyException() {
    }

    public NisNotReadyException(String message) {
        super(message);
    }
}