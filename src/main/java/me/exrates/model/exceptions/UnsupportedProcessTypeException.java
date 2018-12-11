package me.exrates.model.exceptions;

public class UnsupportedProcessTypeException extends RuntimeException {
  public UnsupportedProcessTypeException() {
  }
  
  public UnsupportedProcessTypeException(String message) {
    super(message);
  }
  
  public UnsupportedProcessTypeException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public UnsupportedProcessTypeException(Throwable cause) {
    super(cause);
  }
}
