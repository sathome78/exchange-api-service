package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class MerchantServiceNotFoundException extends RuntimeException{
    public MerchantServiceNotFoundException(String message) {
        super(message);
    }
}
