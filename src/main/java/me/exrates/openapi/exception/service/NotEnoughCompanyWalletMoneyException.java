package me.exrates.openapi.exception.service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class NotEnoughCompanyWalletMoneyException extends RuntimeException {
    public NotEnoughCompanyWalletMoneyException(String message) {
        super(message);
    }
}