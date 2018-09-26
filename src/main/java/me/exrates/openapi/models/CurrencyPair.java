package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.CurrencyPairType;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyPair {

    private int id;
    private String name;
    private Currency currency1;
    private Currency currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;
}
