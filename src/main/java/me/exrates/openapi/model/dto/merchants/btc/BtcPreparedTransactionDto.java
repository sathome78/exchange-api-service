package me.exrates.openapi.model.dto.merchants.btc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter
@ToString
public class BtcPreparedTransactionDto {
    private Map<String, BigDecimal> payments;
    private BigDecimal feeAmount;
    private String hex;

    public BtcPreparedTransactionDto(Map<String, BigDecimal> payments, BigDecimal feeAmount, String hex) {
        this.payments = payments;
        this.feeAmount = feeAmount;
        this.hex = hex;
    }
}
