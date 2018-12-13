package me.exrates.model.dto.merchants.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 18.05.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
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
