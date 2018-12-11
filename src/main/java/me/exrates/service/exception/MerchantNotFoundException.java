package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class MerchantNotFoundException extends RuntimeException{
    public MerchantNotFoundException(String message) {
        super(message);
    }
}
