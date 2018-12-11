package me.exrates.service.exception;

/**
 * Created by OLEG on 06.10.2016.
 */
public class InvalidNicknameException extends RuntimeException {

    public InvalidNicknameException() {
    }

    public InvalidNicknameException(String message) {
        super(message);
    }

    public InvalidNicknameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNicknameException(Throwable cause) {
        super(cause);
    }
}
