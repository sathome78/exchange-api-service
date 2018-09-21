package me.exrates.openapi.exceptions;

/**
 * Created by Valk
 */
public class NoPermissionForOperationException extends RuntimeException {
    public NoPermissionForOperationException() {
    }

    public NoPermissionForOperationException(String message) {
        super(message);
    }
}
