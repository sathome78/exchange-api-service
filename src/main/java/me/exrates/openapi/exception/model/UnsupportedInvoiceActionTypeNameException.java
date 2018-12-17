package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceActionTypeNameException extends RuntimeException {
    public UnsupportedInvoiceActionTypeNameException(String message) {
        super(message);
    }
}
