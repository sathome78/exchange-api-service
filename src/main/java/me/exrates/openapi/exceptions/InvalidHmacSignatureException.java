package me.exrates.openapi.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidHmacSignatureException extends AuthenticationException {

    public InvalidHmacSignatureException(String msg) {
        super(msg);
    }
}
