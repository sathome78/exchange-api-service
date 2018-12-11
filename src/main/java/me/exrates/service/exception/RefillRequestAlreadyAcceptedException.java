package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class RefillRequestAlreadyAcceptedException extends RuntimeException{
    public RefillRequestAlreadyAcceptedException(String message) {
        super(message);
    }
}
