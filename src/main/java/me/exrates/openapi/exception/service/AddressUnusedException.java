package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class AddressUnusedException extends RuntimeException{
    public AddressUnusedException(String message) {
        super(message);
    }
}
