package me.exrates.service.exception;

/**
 * Created by OLEG on 25.05.2017.
 */
public class BtcPaymentNotFoundException extends RuntimeException {
  
  public BtcPaymentNotFoundException() {
  }
  
  public BtcPaymentNotFoundException(String message) {
    super(message);
  }
  
  public BtcPaymentNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public BtcPaymentNotFoundException(Throwable cause) {
    super(cause);
  }
}
