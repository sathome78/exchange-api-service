package me.exrates.model.exceptions;

/**
 * Created by OLEG on 08.09.2016.
 */
public class BigDecimalParseException extends RuntimeException {

    public BigDecimalParseException() {
    }

    public BigDecimalParseException(String message) {
        super(message);
    }

    public BigDecimalParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BigDecimalParseException(Throwable cause) {
        super(cause);
    }
}
