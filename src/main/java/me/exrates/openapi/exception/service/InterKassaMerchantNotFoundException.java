package me.exrates.openapi.exception.service;

public class InterKassaMerchantNotFoundException extends RuntimeException {
    public InterKassaMerchantNotFoundException(final String exceptionMessage) {
        super(exceptionMessage);
    }
}