package me.exrates.model.exceptions;

/**
 * Created by OLEG on 06.04.2017.
 */
public class UnsupportedOrderTypeException extends RuntimeException {
  
  public UnsupportedOrderTypeException() {
  }
  
  public UnsupportedOrderTypeException(String message) {
    super(message);
  }
  
  public UnsupportedOrderTypeException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public UnsupportedOrderTypeException(Throwable cause) {
    super(cause);
  }
}
