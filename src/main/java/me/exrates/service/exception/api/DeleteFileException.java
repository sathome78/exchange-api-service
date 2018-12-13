package me.exrates.service.exception.api;

/**
 * Created by OLEG on 06.10.2016.
 */
public class DeleteFileException extends RuntimeException {
    public DeleteFileException() {
    }

    public DeleteFileException(String message) {
        super(message);
    }

    public DeleteFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteFileException(Throwable cause) {
        super(cause);
    }
}
