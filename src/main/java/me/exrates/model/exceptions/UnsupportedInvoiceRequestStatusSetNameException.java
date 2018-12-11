package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusSetNameException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusSetNameException(String message) {
        super(message);
    }
}
