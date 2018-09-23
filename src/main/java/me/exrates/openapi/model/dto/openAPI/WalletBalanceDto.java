package me.exrates.openapi.model.dto.openAPI;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WalletBalanceDto {

    private String currencyName;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal activeBalance;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal reservedBalance;
}
