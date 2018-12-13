package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForCurrentContextException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrentContextException(String message) {
        super(message);
    }
}
