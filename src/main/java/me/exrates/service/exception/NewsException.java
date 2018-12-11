package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public abstract class NewsException extends RuntimeException {
    public NewsException(String message) {
        super(message);
    }
}
