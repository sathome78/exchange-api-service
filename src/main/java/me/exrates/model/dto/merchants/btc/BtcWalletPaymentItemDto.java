package me.exrates.model.dto.merchants.btc;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcWalletPaymentItemDto {
    private String address;
    private BigDecimal amount;
}
