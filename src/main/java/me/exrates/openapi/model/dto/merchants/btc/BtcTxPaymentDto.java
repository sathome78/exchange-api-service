package me.exrates.openapi.model.dto.merchants.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

/**
 * Created by OLEG on 18.05.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BtcTxPaymentDto {
  
  private String address;
  private String category;
  private BigDecimal amount;
  private BigDecimal fee;
}
