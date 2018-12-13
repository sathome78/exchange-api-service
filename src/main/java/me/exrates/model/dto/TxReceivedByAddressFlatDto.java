package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 23.03.2017.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReceivedByAddressFlatDto {
  private String account;
  private String address;
  private BigDecimal amount;
  private Integer confirmations;
  @JsonProperty(value = "txid")
  private String txId;
  private String category;
  private String abandoned;
}
