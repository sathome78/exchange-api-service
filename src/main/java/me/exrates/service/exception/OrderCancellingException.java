package me.exrates.service.exception;

/**
 * Created by Valk on 23.05.2016.
 */
public class OrderCancellingException extends RuntimeException{
    public OrderCancellingException(String message) {
        super(message);
    }
}
