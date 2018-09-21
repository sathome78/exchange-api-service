package me.exrates.openapi.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusIdException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusIdException(String message) {
        super(message);
    }
}
