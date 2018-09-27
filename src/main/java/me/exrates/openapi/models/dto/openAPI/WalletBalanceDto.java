package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceDto {

    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("active_balance")
    private BigDecimal activeBalance;
    @JsonProperty("reserved_balance")
    private BigDecimal reservedBalance;
}
