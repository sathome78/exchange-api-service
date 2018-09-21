package me.exrates.openapi.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class TransactionPersistException extends RuntimeException {
    public TransactionPersistException(final String message) {
        super(message);
    }
}