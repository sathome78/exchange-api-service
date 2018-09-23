package me.exrates.openapi.exceptions.model;

public class UnsupportedInvoiceStatusForActionException extends RuntimeException {

    public UnsupportedInvoiceStatusForActionException(String message) {
        super(message);
    }
}
