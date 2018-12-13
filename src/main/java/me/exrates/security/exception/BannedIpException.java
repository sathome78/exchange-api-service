package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

public class BannedIpException extends AuthenticationException {

    private long banDurationSeconds;

    public BannedIpException(String msg, long banDurationSeconds) {
        super(msg);
        this.banDurationSeconds = banDurationSeconds;
    }

    public long getBanDurationSeconds() {
        return banDurationSeconds;
    }
}
