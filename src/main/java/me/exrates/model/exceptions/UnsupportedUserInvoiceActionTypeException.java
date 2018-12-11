package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedUserInvoiceActionTypeException extends RuntimeException {
    public UnsupportedUserInvoiceActionTypeException(String message) {
        super(message);
    }
}
