package me.exrates.service.exception;

/**
 * Created by Valk on 17.05.2016.
 */
public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String message) {
        super(message);
    }
}
