package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException extends RuntimeException{
    public RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
