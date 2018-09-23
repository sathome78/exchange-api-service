package me.exrates.openapi.exceptions.model;

public class AvailableForCurrentContextParamNeededForThisActionException extends RuntimeException {

    public AvailableForCurrentContextParamNeededForThisActionException(String message) {
        super(message);
    }
}
