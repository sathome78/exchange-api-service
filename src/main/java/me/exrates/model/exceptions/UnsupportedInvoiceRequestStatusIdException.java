package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusIdException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusIdException(String message) {
        super(message);
    }
}
