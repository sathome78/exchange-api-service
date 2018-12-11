package me.exrates.service.exception;

public class InterKassaMerchantNotFoundException extends RuntimeException {
    public InterKassaMerchantNotFoundException(final String exceptionMessage) {
        super(exceptionMessage);
    }
}