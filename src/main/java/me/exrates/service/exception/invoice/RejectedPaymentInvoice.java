package me.exrates.service.exception.invoice;

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