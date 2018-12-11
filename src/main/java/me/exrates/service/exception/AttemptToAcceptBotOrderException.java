package me.exrates.service.exception;

public class AttemptToAcceptBotOrderException extends OrderAcceptionException {
    public AttemptToAcceptBotOrderException(String message) {
        super(message);
    }
}
