package me.exrates.openapi.exceptions;

/**
 * Created by Maks on 02.10.2017.
 */
public class UnknownNotyPaymentTypeException extends RuntimeException {

    public UnknownNotyPaymentTypeException(String message) {
        super(message);
    }

    public UnknownNotyPaymentTypeException() {
    }
}
