package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class MerchantServiceNotFoundException extends RuntimeException{
    public MerchantServiceNotFoundException(String message) {
        super(message);
    }
}
