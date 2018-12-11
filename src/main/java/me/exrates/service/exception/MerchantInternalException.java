package me.exrates.service.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class MerchantInternalException extends RuntimeException {

    public MerchantInternalException() {/*NOP*/}

    public MerchantInternalException(String message) {
        super(message);
    }

    public MerchantInternalException(final Throwable cause) {
        super(cause);
    }
}