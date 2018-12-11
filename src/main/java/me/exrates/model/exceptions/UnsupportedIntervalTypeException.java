package me.exrates.model.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedIntervalTypeException extends RuntimeException {

    public UnsupportedIntervalTypeException(String intervalType) {
        super("No such interval type " + intervalType);
    }
}