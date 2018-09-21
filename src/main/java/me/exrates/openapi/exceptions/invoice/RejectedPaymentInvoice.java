package me.exrates.openapi.exceptions.invoice;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class RejectedPaymentInvoice extends RuntimeException {
    public RejectedPaymentInvoice() {
    }

    public RejectedPaymentInvoice(String message) {
        super(message);
    }
}