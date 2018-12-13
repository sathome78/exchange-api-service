package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {
    public InvoiceActionIsProhibitedForNotHolderException(String message) {
        super(message);
    }
}
