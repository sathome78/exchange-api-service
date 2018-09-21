package me.exrates.openapi.model.exceptions;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForCurrencyPermissionOperationException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String message) {
        super(message);
    }
}
