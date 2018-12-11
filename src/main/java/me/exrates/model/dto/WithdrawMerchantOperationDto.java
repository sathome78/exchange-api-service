package me.exrates.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by ValkSam on 24.03.2017.
 */
@Getter
@ToString
@Builder
public class WithdrawMerchantOperationDto {
  private String currency;
  private String amount;
  private String accountTo;
  private String destinationTag;
}
