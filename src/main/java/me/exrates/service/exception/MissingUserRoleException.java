package me.exrates.service.exception;

/**
 * Created by OLEG on 24.01.2017.
 */
public class MissingUserRoleException extends RuntimeException {
    public MissingUserRoleException() {
    }

    public MissingUserRoleException(String message) {
        super(message);
    }

    public MissingUserRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingUserRoleException(Throwable cause) {
        super(cause);
    }
}
