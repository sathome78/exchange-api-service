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
public class RefillRequestPutOnBchExamDto {
  private Integer requestId;
  private Integer merchantId;
  private Integer currencyId;
  private BigDecimal amount;
  private String address;
  private String hash;
  private String blockhash;
}
