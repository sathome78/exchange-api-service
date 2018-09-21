package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 23.03.2017.
 */
@Getter @Setter
@ToString
public class TxReceivedByAddressFlatDto {
  private String account;
  private String address;
  private BigDecimal amount;
  private Integer confirmations;
  private String txId;
}
