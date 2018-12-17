package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class RefillRequestAlreadyAcceptedException extends RuntimeException{
    public RefillRequestAlreadyAcceptedException(String message) {
        super(message);
    }
}
