package me.exrates.service.exception.api;

/**
 * Created by OLEG on 08.09.2016.
 */
public class UniqueNicknameConstraintException extends RuntimeException {
    public UniqueNicknameConstraintException() {
    }

    public UniqueNicknameConstraintException(String message) {
        super(message);
    }

    public UniqueNicknameConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueNicknameConstraintException(Throwable cause) {
        super(cause);
    }
}
