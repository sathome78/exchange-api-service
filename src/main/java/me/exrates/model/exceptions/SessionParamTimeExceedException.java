package me.exrates.model.exceptions;

public class SessionParamTimeExceedException extends RuntimeException {

    public SessionParamTimeExceedException() {
    }

    public SessionParamTimeExceedException(String message) {
        super(message);
    }
}
