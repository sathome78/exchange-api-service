package me.exrates.service.exception.invoice;

/**
 * Created by Valk
 */
public class InvoiceNotFoundException extends RuntimeException{
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
