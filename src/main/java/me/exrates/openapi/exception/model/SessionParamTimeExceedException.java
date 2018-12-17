package me.exrates.openapi.exception.model;

public class SessionParamTimeExceedException extends RuntimeException {

    public SessionParamTimeExceedException() {
    }

    public SessionParamTimeExceedException(String message) {
        super(message);
    }
}
