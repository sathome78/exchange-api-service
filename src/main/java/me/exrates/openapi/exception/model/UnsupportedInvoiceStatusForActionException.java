package me.exrates.openapi.exception.model;

/**
 * Created by ValkSam
 */
public class UnsupportedInvoiceStatusForActionException extends RuntimeException {
  public UnsupportedInvoiceStatusForActionException(String message) {
    super(message);
  }
}
