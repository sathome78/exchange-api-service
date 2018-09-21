package me.exrates.openapi.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class NotEnoughUserWalletMoneyException extends RuntimeException {
    public NotEnoughUserWalletMoneyException(String message) {
        super(message);
    }
}