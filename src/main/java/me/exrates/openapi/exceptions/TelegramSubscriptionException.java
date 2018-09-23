package me.exrates.openapi.exceptions;

public class TelegramSubscriptionException extends RuntimeException {

    public TelegramSubscriptionException() {
    }

    public TelegramSubscriptionException(String message) {
        super(message);
    }
}
