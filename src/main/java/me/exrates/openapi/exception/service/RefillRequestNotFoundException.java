package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class RefillRequestNotFoundException extends RuntimeException{
    public RefillRequestNotFoundException(String message) {
        super(message);
    }
}
