package me.exrates.openapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class ExternalReservedWalletAddressDto {

    private Integer id;
    private Integer currencyId;
    private String name;
    private String walletAddress;
    private BigDecimal balance;
}
