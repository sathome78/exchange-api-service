package me.exrates.service.exception;

/**
 * Created by Maks on 09.10.2017.
 */
public class InvalidRefNumberException extends RuntimeException {

    public InvalidRefNumberException() {
    }

    public InvalidRefNumberException(String message) {
        super(message);
    }
}
