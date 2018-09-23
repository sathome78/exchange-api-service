package me.exrates.openapi.exceptions;

public class MerchantException extends RuntimeException {

    private final String REASON_CODE = "withdraw.reject.reason.general";

    public MerchantException() {
    }

    public MerchantException(String message) {
        super(message);
    }

    public MerchantException(String message, Throwable cause) {
        super(message, cause);
    }

    public MerchantException(Throwable cause) {
        super(cause);
    }

    public String getReason() {
        return REASON_CODE;
    }
}
