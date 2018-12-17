package me.exrates.openapi.exception.service;

public class MerchantSpecParamNotFoundException extends RuntimeException {
    public MerchantSpecParamNotFoundException() {
    }

    public MerchantSpecParamNotFoundException(String message) {
        super(message);
    }

    public MerchantSpecParamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MerchantSpecParamNotFoundException(Throwable cause) {
        super(cause);
    }
}
