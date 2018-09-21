package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class RefillRequestNotFoundException extends RuntimeException {
    public RefillRequestNotFoundException(String message) {
        super(message);
    }
}
