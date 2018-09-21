package me.exrates.openapi.exceptions;

public class UserCommentNotFoundException extends RuntimeException {
    public UserCommentNotFoundException() {
    }

    public UserCommentNotFoundException(String message) {
        super(message);
    }

    public UserCommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCommentNotFoundException(Throwable cause) {
        super(cause);
    }
}
