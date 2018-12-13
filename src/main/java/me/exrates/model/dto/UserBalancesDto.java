package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class UserBalancesDto {

    private int userId;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
}
