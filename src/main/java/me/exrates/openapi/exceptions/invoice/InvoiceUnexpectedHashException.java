package me.exrates.openapi.exceptions.invoice;

/**
 * Created by Valk
 */
public class InvoiceUnexpectedHashException extends RuntimeException {
    public InvoiceUnexpectedHashException(String message) {
        super(message);
    }
}
