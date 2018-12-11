package me.exrates.service.exception;

import me.exrates.service.exception.invoice.MerchantException;

/**
 * Created by Valk on 04.04.16.
 */
public class WrongFinPasswordException extends MerchantException {

    private final String REASON_CODE = "admin.wrongfinpassword";

    @Override
    public String getReason() {
        return REASON_CODE;
    }

    public WrongFinPasswordException() {}

    public WrongFinPasswordException(String message) {
        super(message);
    }
}
