package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(exclude = {"user"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    private int id;
    private int currencyId;
    private User user;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private String name;

    public Wallet(int currencyId, User user) {
        this.currencyId = currencyId;
        this.user = user;
        this.activeBalance = BigDecimal.ZERO;
    }
}