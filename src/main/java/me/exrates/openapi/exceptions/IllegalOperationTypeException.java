package me.exrates.openapi.exceptions;

/**
 * Created by Valk
 */
public class IllegalOperationTypeException extends RuntimeException {

    public IllegalOperationTypeException(String message) {
        super(message);
    }

    public IllegalOperationTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
