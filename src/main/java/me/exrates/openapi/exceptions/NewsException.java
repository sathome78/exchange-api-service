package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public abstract class NewsException extends RuntimeException {
    public NewsException(String message) {
        super(message);
    }
}
