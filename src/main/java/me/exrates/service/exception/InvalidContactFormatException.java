package me.exrates.service.exception;

/**
 * Created by Maks on 09.10.2017.
 */
public class InvalidContactFormatException extends RuntimeException {

    public InvalidContactFormatException() {
    }

    public InvalidContactFormatException(String message) {
        super(message);
    }
}
