package me.exrates.service.exception;

/**
 * Created by Valk on 12.05.2016.
 */
public class OperationCausedNegativeBalance extends RuntimeException{
    public OperationCausedNegativeBalance(String message) {
        super(message);
    }
}
