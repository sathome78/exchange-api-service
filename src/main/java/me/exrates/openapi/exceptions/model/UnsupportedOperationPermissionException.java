package me.exrates.openapi.exceptions.model;

public class UnsupportedOperationPermissionException extends RuntimeException {

    public UnsupportedOperationPermissionException() {
    }

    public UnsupportedOperationPermissionException(String message) {
        super(message);
    }

    public UnsupportedOperationPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedOperationPermissionException(Throwable cause) {
        super(cause);
    }
}
