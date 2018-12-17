package me.exrates.openapi.exception.dao;

/**
 * Created by ValkSam
 */
public class DuplicatedMerchantTransactionIdOrAttemptToRewriteException extends Exception{
    public DuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
