package me.exrates.openapi.model.dto.merchants.btc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcWalletPaymentItemDto {
    private String address;
    private BigDecimal amount;
}
