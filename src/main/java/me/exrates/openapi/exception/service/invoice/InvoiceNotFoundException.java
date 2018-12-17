package me.exrates.openapi.exception.service.invoice;

/**
 * Created by Valk
 */
public class InvoiceNotFoundException extends RuntimeException{
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
