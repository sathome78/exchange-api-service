package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class MerchantNotFoundException extends RuntimeException{
    public MerchantNotFoundException(String message) {
        super(message);
    }
}
