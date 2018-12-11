package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedUserActionOnInvoiceException extends RuntimeException {
    public UnsupportedUserActionOnInvoiceException(String message) {
        super(message);
    }
}
