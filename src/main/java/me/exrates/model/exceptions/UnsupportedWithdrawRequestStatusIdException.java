package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class UnsupportedWithdrawRequestStatusIdException extends RuntimeException {
    public UnsupportedWithdrawRequestStatusIdException(String message) {
        super(message);
    }
}
