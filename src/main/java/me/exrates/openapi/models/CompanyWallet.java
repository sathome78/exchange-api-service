package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(exclude = {"currency"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class CompanyWallet {

    private int id;
    private Currency currency;
    private BigDecimal balance;
    private BigDecimal commissionBalance;
}