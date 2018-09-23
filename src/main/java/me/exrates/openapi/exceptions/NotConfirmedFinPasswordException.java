package me.exrates.openapi.exceptions;

public class NotConfirmedFinPasswordException extends MerchantException {

    private final String REASON_CODE = "admin.notconfirmedfinpassword";

    @Override
    public String getReason() {
        return REASON_CODE;
    }

    public NotConfirmedFinPasswordException() {
    }

    public NotConfirmedFinPasswordException(String message) {
        super(message);
    }
}
