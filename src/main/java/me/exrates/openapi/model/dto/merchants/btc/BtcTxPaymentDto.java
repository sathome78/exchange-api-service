package me.exrates.openapi.model.dto.merchants.btc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 18.05.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTxPaymentDto {
  
  private String address;
  private String category;
  private BigDecimal amount;
  private BigDecimal fee;
}
