package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class PermittedOperationParamNeededForThisActionException extends RuntimeException {
    public PermittedOperationParamNeededForThisActionException(String message) {
        super(message);
    }
}
