package me.exrates.openapi.exceptions;

import me.exrates.openapi.model.enums.WalletTransferStatus;

public class PaymentException extends RuntimeException {

    public PaymentException(WalletTransferStatus walletTransferStatus) {
    }
}
