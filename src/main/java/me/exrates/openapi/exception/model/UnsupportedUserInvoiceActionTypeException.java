package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedUserInvoiceActionTypeException extends RuntimeException {
    public UnsupportedUserInvoiceActionTypeException(String message) {
        super(message);
    }
}
