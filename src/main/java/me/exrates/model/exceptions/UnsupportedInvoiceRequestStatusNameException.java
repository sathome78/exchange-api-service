package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceRequestStatusNameException extends RuntimeException {
    public UnsupportedInvoiceRequestStatusNameException(String message) {
        super(message);
    }
}
