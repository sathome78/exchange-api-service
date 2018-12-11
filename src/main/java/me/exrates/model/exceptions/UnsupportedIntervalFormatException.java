package me.exrates.model.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedIntervalFormatException extends RuntimeException {

    public UnsupportedIntervalFormatException(String intervalString) {
        super("No such interval format " + intervalString);
    }
}