package me.exrates.openapi.exception.service;

public class CallBackUrlAlreadyExistException extends Exception {
    public CallBackUrlAlreadyExistException(String callback_already_present) {
        super(callback_already_present);
    }
}
