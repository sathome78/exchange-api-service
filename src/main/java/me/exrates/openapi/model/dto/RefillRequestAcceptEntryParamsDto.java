package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class RefillRequestAcceptEntryParamsDto {
  private Integer requestId;
  private BigDecimal amount;
  private String remark;
  private String merchantTxId;
}
