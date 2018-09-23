package me.exrates.openapi.exceptions.model;

public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {

    public InvoiceActionIsProhibitedForNotHolderException(String message) {
        super(message);
    }
}
