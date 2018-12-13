package me.exrates.service.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class WalletPersistException extends RuntimeException {
    public WalletPersistException(String message) {
        super(message);
    }
}