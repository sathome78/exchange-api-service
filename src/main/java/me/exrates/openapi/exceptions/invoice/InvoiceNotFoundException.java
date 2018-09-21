package me.exrates.openapi.exceptions.invoice;

/**
 * Created by Valk
 */
public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
