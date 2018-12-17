package me.exrates.openapi.exception.service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() {
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}