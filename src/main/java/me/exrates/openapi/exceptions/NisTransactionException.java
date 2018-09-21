package me.exrates.openapi.exceptions;

/**
 * Created by maks on 20.07.2017.
 */
public class NisTransactionException extends RuntimeException {

    public NisTransactionException() {
    }

    public NisTransactionException(String message) {
        super(message);
    }
}
