package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedWithdrawRequestStatusNameException extends RuntimeException {
    public UnsupportedWithdrawRequestStatusNameException(String message) {
        super(message);
    }
}
