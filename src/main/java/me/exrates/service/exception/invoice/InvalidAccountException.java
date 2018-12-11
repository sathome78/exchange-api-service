package me.exrates.service.exception.invoice;

/**
 * Created by OLEG on 28.04.2017.
 */
public class InvalidAccountException extends MerchantException {
  private final String REASON_CODE = "withdraw.reject.reason.invalidAccount";
  
  public InvalidAccountException() {
  }
  
  public InvalidAccountException(String message) {
    super(message);
  }
  
  public InvalidAccountException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InvalidAccountException(Throwable cause) {
    super(cause);
  }
  
  @Override
  public String getReason() {
    return REASON_CODE;
  }
}
