package me.exrates.openapi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 19.03.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTransactionShort {
  private Integer invoiceId;
  private String btcTransactionIdHash;
  private BigDecimal amount;
  private Integer confirmations;
}
