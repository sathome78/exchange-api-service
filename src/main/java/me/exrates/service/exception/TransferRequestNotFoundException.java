package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class TransferRequestNotFoundException extends RuntimeException{
    public TransferRequestNotFoundException(String message) {
        super(message);
    }
}
