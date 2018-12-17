package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class TransferRequestNotFoundException extends RuntimeException{
    public TransferRequestNotFoundException(String message) {
        super(message);
    }
}
