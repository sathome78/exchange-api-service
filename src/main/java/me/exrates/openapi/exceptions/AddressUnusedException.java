package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class AddressUnusedException extends RuntimeException {
    public AddressUnusedException(String message) {
        super(message);
    }
}
