package me.exrates.openapi.model.exceptions;

/**
 * Created by Valk on 16.05.2016.
 */
public class UnsupportedTransactionStatusException extends RuntimeException {
    public UnsupportedTransactionStatusException(int transactionStatusId) {
        super("No such transaction status " + transactionStatusId);
    }
}