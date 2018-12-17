package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusIdException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusIdException(String message) {
        super(message);
    }
}
