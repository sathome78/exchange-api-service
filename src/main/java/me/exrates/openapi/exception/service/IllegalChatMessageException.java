package me.exrates.openapi.exception.service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class IllegalChatMessageException extends Exception {

    public IllegalChatMessageException(String message) {
        super(message);
    }

    public IllegalChatMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
