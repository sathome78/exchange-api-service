package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceActionTypeNameException extends RuntimeException {
    public UnsupportedInvoiceActionTypeNameException(String message) {
        super(message);
    }
}
