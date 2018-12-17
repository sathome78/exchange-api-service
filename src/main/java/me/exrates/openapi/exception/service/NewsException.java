package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public abstract class NewsException extends RuntimeException {
    public NewsException(String message) {
        super(message);
    }
}
