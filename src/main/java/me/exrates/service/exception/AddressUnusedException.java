package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class AddressUnusedException extends RuntimeException{
    public AddressUnusedException(String message) {
        super(message);
    }
}
