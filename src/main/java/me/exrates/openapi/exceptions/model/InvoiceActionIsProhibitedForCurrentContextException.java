package me.exrates.openapi.exceptions.model;

public class InvoiceActionIsProhibitedForCurrentContextException extends RuntimeException {

    public InvoiceActionIsProhibitedForCurrentContextException(String message) {
        super(message);
    }
}
