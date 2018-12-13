package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class WithdrawRequestDeclineException extends RuntimeException{
    public WithdrawRequestDeclineException(String message) {
        super(message);
    }
}
