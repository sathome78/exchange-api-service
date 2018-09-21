package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class MerchantServiceNotFoundException extends RuntimeException {
    public MerchantServiceNotFoundException(String message) {
        super(message);
    }
}
