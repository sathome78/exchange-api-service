package me.exrates.service.exception.invoice;

/**
 * Created by OLEG on 28.04.2017.
 */
public class InsufficientCostsInWalletException extends MerchantException {
  private final String REASON_CODE = "withdraw.reject.reason.timeoutExceeded";
  
  public InsufficientCostsInWalletException() {
  }
  
  public InsufficientCostsInWalletException(String message) {
    super(message);
  }
  
  public InsufficientCostsInWalletException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InsufficientCostsInWalletException(Throwable cause) {
    super(cause);
  }
  
  @Override
  public String getReason() {
    return REASON_CODE;
  }
}
