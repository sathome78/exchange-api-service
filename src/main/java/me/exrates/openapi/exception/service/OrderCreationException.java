package me.exrates.openapi.exception.service;

/**
 * Created by Valk on 23.05.2016.
 */
public class OrderCreationException extends RuntimeException{
    public OrderCreationException(String message) {
        super(message);
    }
}