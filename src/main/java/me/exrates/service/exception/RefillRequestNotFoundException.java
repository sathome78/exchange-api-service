package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class RefillRequestNotFoundException extends RuntimeException{
    public RefillRequestNotFoundException(String message) {
        super(message);
    }
}
