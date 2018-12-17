package me.exrates.openapi.exception.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidTimestampException extends AuthenticationException {

    public InvalidTimestampException(String message) {
        super(message);
    }

    public InvalidTimestampException(String msg, Throwable t) {
        super(msg, t);
    }
}
