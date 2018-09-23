package me.exrates.openapi.exceptions;

public class AlreadyAcceptedOrderException extends OrderAcceptionException {

    public AlreadyAcceptedOrderException(String message) {
        super(message);
    }
}
