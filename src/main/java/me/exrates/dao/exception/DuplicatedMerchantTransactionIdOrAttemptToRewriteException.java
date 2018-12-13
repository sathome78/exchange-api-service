package me.exrates.dao.exception;

/**
 * Created by ValkSam
 */
public class DuplicatedMerchantTransactionIdOrAttemptToRewriteException extends Exception{
    public DuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
