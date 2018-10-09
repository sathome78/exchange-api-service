package me.exrates.openapi.exceptions;

import org.springframework.security.core.AuthenticationException;

public class MissingAuthHeaderException extends AuthenticationException {

    public MissingAuthHeaderException(String message) {
        super(message);
    }
}
