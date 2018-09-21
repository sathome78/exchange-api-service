package me.exrates.openapi.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * @author ValkSam
 */
@Builder
@Getter @Setter
@ToString
public class RefillRequestAcceptDto {
  private Integer requestId;
  private Integer merchantId;
  private Integer currencyId;
  private BigDecimal amount;
  private String address;
  private String merchantTransactionId;
  private Integer requesterAdminId;
  private String remark;
  private boolean toMainAccountTransferringConfirmNeeded;
  private Predicate<RefillRequestFlatDto> predicate;

  public static RefillRequestAcceptDto of(RefillRequestSetConfirmationsNumberDto confirmationsNumberDto) {
    return new RefillRequestAcceptDto(
            confirmationsNumberDto.getRequestId(),
            confirmationsNumberDto.getMerchantId(),
            confirmationsNumberDto.getCurrencyId(),
            confirmationsNumberDto.getAmount(),
            confirmationsNumberDto.getAddress(),
            confirmationsNumberDto.getHash(),
            null, null, false, null
    );
  }
}
