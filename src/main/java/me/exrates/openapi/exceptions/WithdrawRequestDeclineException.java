package me.exrates.openapi.exceptions;

/**
 * Created by ValkSam
 */
public class WithdrawRequestDeclineException extends RuntimeException {
    public WithdrawRequestDeclineException(String message) {
        super(message);
    }
}
