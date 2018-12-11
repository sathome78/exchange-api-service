package me.exrates.service.exception;

import me.exrates.model.enums.WalletTransferStatus;

/**
 * Created by Maks on 03.10.2017.
 */
public class PaymentException extends RuntimeException {
    public PaymentException(WalletTransferStatus walletTransferStatus) {
    }
}
