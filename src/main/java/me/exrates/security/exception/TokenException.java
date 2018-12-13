package me.exrates.security.exception;

import me.exrates.service.exception.api.ErrorCode;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by OLEG on 12.09.2016.
 */
public class TokenException extends AuthenticationException {
    private final ErrorCode errorCode;

    public TokenException(String msg, Throwable t) {
        super(msg, t);
        this.errorCode = ErrorCode.FAILED_AUTHENTICATION;
    }

    public TokenException(String msg) {
        super(msg);
        this.errorCode = ErrorCode.FAILED_AUTHENTICATION;
    }

    public TokenException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public TokenException(String msg, Throwable t, ErrorCode errorCode) {
        super(msg, t);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
