package me.exrates.service.exception;

/**
 * Created by OLEG on 16.03.2017.
 */
public class BitcoinCoreException extends RuntimeException {
  
  public BitcoinCoreException() {
  }
  
  public BitcoinCoreException(String message) {
    super(message);
  }
  
  public BitcoinCoreException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public BitcoinCoreException(Throwable cause) {
    super(cause);
  }
}
