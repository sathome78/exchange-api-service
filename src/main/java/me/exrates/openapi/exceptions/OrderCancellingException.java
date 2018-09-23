package me.exrates.openapi.exceptions;

public class OrderCancellingException extends RuntimeException {

    public OrderCancellingException(String message) {
        super(message);
    }
}
