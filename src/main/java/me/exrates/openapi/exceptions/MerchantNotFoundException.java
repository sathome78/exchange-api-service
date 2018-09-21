package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class MerchantNotFoundException extends RuntimeException {
    public MerchantNotFoundException(String message) {
        super(message);
    }
}
