package me.exrates.service.exception.invoice;

/**
 * Created by Valk
 */
public class IllegalInvoiceStatusException extends Exception {

    public IllegalInvoiceStatusException(String message) {
        super(message);
    }
}
