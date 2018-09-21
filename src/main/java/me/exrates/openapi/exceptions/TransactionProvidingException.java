package me.exrates.openapi.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class TransactionProvidingException extends RuntimeException {
    public TransactionProvidingException(String message) {
        super(message);
    }
}