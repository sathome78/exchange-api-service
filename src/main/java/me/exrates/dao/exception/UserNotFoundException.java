package me.exrates.dao.exception;

/**
 * Created by ValkSam
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
