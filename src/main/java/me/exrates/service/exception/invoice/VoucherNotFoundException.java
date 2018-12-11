package me.exrates.service.exception.invoice;

public class VoucherNotFoundException extends RuntimeException {

    public VoucherNotFoundException() {
    }

    public VoucherNotFoundException(String message) {
        super(message);
    }

    public VoucherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public VoucherNotFoundException(Throwable cause) {
        super(cause);
    }
}
