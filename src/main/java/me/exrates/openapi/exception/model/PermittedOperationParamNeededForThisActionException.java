package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class PermittedOperationParamNeededForThisActionException extends RuntimeException {
    public PermittedOperationParamNeededForThisActionException(String message) {
        super(message);
    }
}
