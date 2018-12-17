package me.exrates.openapi.exception.model;

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
