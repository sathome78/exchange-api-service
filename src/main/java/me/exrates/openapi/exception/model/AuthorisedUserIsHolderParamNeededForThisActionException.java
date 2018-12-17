package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class AuthorisedUserIsHolderParamNeededForThisActionException extends RuntimeException {
    public AuthorisedUserIsHolderParamNeededForThisActionException(String message) {
        super(message);
    }
}
