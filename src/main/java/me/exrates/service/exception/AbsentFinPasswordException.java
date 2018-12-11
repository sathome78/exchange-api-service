package me.exrates.service.exception;

import me.exrates.service.exception.invoice.MerchantException;

/**
 * Created by Valk on 04.04.16.
 */
public class AbsentFinPasswordException extends MerchantException {

    private final String REASON_CODE = "admin.absentfinpassword";

    @Override
    public String getReason() {
        return REASON_CODE;
    }

    public AbsentFinPasswordException() {
    }

    public AbsentFinPasswordException(String message) {
        super(message);
    }
}
