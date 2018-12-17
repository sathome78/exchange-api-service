package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class TransactionLabelTypeMoreThenOneResultException extends RuntimeException {
    public TransactionLabelTypeMoreThenOneResultException(String message) {
        super(message);
    }
}
