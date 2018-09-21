package me.exrates.openapi.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedUserActionOnInvoiceException extends RuntimeException {
    public UnsupportedUserActionOnInvoiceException(String message) {
        super(message);
    }
}
