package me.exrates.service.exception;

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