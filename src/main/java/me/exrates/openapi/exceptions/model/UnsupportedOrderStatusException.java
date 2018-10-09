package me.exrates.openapi.exceptions.model;

public class UnsupportedOrderStatusException extends RuntimeException {

    public UnsupportedOrderStatusException(int tupleId) {
        super("No such order status " + tupleId);
    }
}