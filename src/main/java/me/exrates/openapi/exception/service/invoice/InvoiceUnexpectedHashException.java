package me.exrates.openapi.exception.service.invoice;

/**
 * Created by Valk
 */
public class InvoiceUnexpectedHashException extends RuntimeException{
    public InvoiceUnexpectedHashException(String message) {
        super(message);
    }
}
