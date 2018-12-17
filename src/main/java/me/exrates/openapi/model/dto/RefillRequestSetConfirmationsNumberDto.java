package me.exrates.openapi.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Builder
@Getter
@Setter
@ToString
public class RefillRequestSetConfirmationsNumberDto {
  private Integer requestId;
  private String address;
  private Integer merchantId;
  private Integer currencyId;
  private Integer confirmations;
  private String hash;
  private BigDecimal amount;
  private String blockhash;
}
