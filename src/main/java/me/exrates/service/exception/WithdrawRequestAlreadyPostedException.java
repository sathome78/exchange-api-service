package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class WithdrawRequestAlreadyPostedException extends RuntimeException{
    public WithdrawRequestAlreadyPostedException(String message) {
        super(message);
    }
}
