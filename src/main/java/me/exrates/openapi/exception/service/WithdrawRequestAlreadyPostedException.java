package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class WithdrawRequestAlreadyPostedException extends RuntimeException{
    public WithdrawRequestAlreadyPostedException(String message) {
        super(message);
    }
}
