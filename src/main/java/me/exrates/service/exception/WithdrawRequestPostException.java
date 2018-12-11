package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class WithdrawRequestPostException extends RuntimeException{
    public WithdrawRequestPostException(String message) {
        super(message);
    }
}
