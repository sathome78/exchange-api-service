package me.exrates.openapi.exception.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidHmacSignatureException extends AuthenticationException {
    public InvalidHmacSignatureException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidHmacSignatureException(String msg) {
        super(msg);
    }
}
