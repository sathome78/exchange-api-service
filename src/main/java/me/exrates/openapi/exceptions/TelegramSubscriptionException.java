package me.exrates.openapi.exceptions;

/**
 * Created by Maks on 05.10.2017.
 */
public class TelegramSubscriptionException extends RuntimeException {

    public TelegramSubscriptionException() {
    }

    public TelegramSubscriptionException(String message) {
        super(message);
    }
}
