package me.exrates.service.exception;

/**
 * Created by Valk
 */
public class NoPermissionForOperationException extends RuntimeException{
    public NoPermissionForOperationException() {
    }

    public NoPermissionForOperationException(String message) {
        super(message);
    }
}
