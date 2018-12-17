package me.exrates.openapi.exception.service.invoice;

/**
 * Created by Valk
 */
public class IllegalInvoiceAmountException extends Exception {

    public IllegalInvoiceAmountException(String message) {
        super(message);
    }

    public IllegalInvoiceAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
