package me.exrates.service.exception;

/**
 * Created by OLEG on 23.12.2016.
 */
public class MerchantCurrencyBlockedException extends RuntimeException {

    public MerchantCurrencyBlockedException() {
    }

    public MerchantCurrencyBlockedException(String message) {
        super(message);
    }

    public MerchantCurrencyBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MerchantCurrencyBlockedException(Throwable cause) {
        super(cause);
    }
}
