package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {
    public InvoiceActionIsProhibitedForNotHolderException(String message) {
        super(message);
    }
}
