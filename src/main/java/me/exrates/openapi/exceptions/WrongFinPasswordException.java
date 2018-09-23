package me.exrates.openapi.exceptions;

public class WrongFinPasswordException extends MerchantException {

    private final String REASON_CODE = "admin.wrongfinpassword";

    @Override
    public String getReason() {
        return REASON_CODE;
    }

    public WrongFinPasswordException() {
    }

    public WrongFinPasswordException(String message) {
        super(message);
    }
}
