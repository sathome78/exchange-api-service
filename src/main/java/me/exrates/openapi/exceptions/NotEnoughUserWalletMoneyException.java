package me.exrates.openapi.exceptions;

public class NotEnoughUserWalletMoneyException extends RuntimeException {

    public NotEnoughUserWalletMoneyException(String message) {
        super(message);
    }
}