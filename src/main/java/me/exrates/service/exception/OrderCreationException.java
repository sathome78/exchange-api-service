package me.exrates.service.exception;

/**
 * Created by Valk on 23.05.2016.
 */
public class OrderCreationException extends RuntimeException{
    public OrderCreationException(String message) {
        super(message);
    }
}
