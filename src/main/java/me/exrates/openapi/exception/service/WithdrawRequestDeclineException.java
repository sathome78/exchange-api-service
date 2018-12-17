package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class WithdrawRequestDeclineException extends RuntimeException{
    public WithdrawRequestDeclineException(String message) {
        super(message);
    }
}
