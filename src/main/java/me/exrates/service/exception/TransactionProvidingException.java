package me.exrates.service.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class TransactionProvidingException extends RuntimeException {
    public TransactionProvidingException(String message) {
        super(message);
    }
}