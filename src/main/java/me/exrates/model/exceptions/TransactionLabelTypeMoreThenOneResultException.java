package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class TransactionLabelTypeMoreThenOneResultException extends RuntimeException {
    public TransactionLabelTypeMoreThenOneResultException(String message) {
        super(message);
    }
}
