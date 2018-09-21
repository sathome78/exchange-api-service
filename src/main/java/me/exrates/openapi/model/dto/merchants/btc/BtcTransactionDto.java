package me.exrates.openapi.model.dto.merchants.btc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 18.05.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTransactionDto {
  
  private BigDecimal amount;
  private BigDecimal fee;
  private Integer confirmations;
  private String txId;
  private String blockhash;
  private List<String> walletConflicts;
  private Long time;
  private Long timeReceived;
  private String comment;
  private String to;
  private List<BtcTxPaymentDto> details;
  
}
