package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusSetNameException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusSetNameException(String message) {
        super(message);
    }
}
