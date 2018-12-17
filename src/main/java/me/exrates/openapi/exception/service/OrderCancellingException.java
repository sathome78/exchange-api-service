package me.exrates.openapi.exception.service;

/**
 * Created by Valk on 23.05.2016.
 */
public class OrderCancellingException extends RuntimeException{
    public OrderCancellingException(String message) {
        super(message);
    }
}
