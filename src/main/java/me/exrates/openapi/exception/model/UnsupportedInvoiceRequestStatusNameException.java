package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusNameException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusNameException(String message) {
        super(message);
    }
}
