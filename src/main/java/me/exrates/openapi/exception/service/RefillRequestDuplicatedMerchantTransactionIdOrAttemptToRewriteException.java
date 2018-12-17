package me.exrates.openapi.exception.service;

/**
 * Created by ValkSam
 */
public class RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException extends RuntimeException{
    public RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
