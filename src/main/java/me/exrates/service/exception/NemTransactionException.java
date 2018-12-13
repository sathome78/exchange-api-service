package me.exrates.service.exception;

/**
 * Created by maks on 20.07.2017.
 */
public class NemTransactionException extends RuntimeException{

    public NemTransactionException() {
    }

    public NemTransactionException(String message) {
        super(message);
    }
}
