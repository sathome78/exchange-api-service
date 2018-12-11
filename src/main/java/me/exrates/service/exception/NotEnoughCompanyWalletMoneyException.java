package me.exrates.service.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class NotEnoughCompanyWalletMoneyException extends RuntimeException {
    public NotEnoughCompanyWalletMoneyException(String message) {
        super(message);
    }
}