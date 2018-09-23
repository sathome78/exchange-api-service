package me.exrates.openapi.exceptions.model;

public class PermittedOperationParamNeededForThisActionException extends RuntimeException {

    public PermittedOperationParamNeededForThisActionException(String message) {
        super(message);
    }
}
