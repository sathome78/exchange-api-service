package me.exrates.service.exception;

public class CommentNonEditableException extends  RuntimeException {
    public CommentNonEditableException() {
    }

    public CommentNonEditableException(String message) {
        super(message);
    }

    public CommentNonEditableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNonEditableException(Throwable cause) {
        super(cause);
    }
}
