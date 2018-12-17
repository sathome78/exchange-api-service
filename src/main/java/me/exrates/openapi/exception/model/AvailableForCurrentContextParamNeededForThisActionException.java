package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class AvailableForCurrentContextParamNeededForThisActionException extends RuntimeException {
    public AvailableForCurrentContextParamNeededForThisActionException(String message) {
        super(message);
    }
}
