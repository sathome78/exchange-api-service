package me.exrates.service.exception;

public class EthereumException extends RuntimeException {

  public EthereumException() {
  }

  public EthereumException(String message) {
    super(message);
  }

  public EthereumException(String message, Throwable cause) {
    super(message, cause);
  }

  public EthereumException(Throwable cause) {
    super(cause);
  }
}
