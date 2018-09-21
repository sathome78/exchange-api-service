package me.exrates.openapi.exceptions;

/**
 * Created by Valk
 */
public class IllegalTransactionProvidedStatusException extends Exception {

    public IllegalTransactionProvidedStatusException(String message) {
        super(message);
    }

    public IllegalTransactionProvidedStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
