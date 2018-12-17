package me.exrates.openapi.exception.dao;

/**
 * Created by ValkSam
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
