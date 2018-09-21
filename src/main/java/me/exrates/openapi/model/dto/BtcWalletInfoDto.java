package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by OLEG on 24.03.2017.
 */
@Getter @Setter
@ToString
public class BtcWalletInfoDto {
  private String balance;
  private String confirmedNonSpendableBalance;
  private String unconfirmedBalance;
  private Integer transactionCount;
}
