package me.exrates.openapi.exception.service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedMerchantException extends RuntimeException {

    public UnsupportedMerchantException(String message) {
        super(message);
    }
}