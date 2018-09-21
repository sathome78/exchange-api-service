package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class RefillRequestAlreadyAcceptedException extends RuntimeException {
    public RefillRequestAlreadyAcceptedException(String message) {
        super(message);
    }
}
