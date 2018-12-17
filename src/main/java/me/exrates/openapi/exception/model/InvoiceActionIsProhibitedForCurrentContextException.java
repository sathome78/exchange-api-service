package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForCurrentContextException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrentContextException(String message) {
        super(message);
    }
}
