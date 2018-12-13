package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class AuthorisedUserIsHolderParamNeededForThisActionException extends RuntimeException {
    public AuthorisedUserIsHolderParamNeededForThisActionException(String message) {
        super(message);
    }
}
