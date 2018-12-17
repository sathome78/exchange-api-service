package me.exrates.openapi.exception.service;

import me.exrates.openapi.model.enums.WalletTransferStatus;

/**
 * Created by Maks on 03.10.2017.
 */
public class PaymentException extends RuntimeException {
    public PaymentException(WalletTransferStatus walletTransferStatus) {
    }
}
