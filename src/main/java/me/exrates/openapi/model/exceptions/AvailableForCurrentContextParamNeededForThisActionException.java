package me.exrates.openapi.model.exceptions;

/**
 * Created by ValkSam
 */
public class AvailableForCurrentContextParamNeededForThisActionException extends RuntimeException {
    public AvailableForCurrentContextParamNeededForThisActionException(String message) {
        super(message);
    }
}
