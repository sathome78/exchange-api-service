package me.exrates.openapi.model.vo;

import lombok.*;

import java.math.BigDecimal;

/**
 * Created by OLEG on 19.03.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTransactionShort {
  private Integer invoiceId;
  private String btcTransactionIdHash;
  private BigDecimal amount;
  private Integer confirmations;
}
