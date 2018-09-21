package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CurrencyPair;

/**
 * Created by Valk on 02.06.2016.
 */
@Getter @Setter
@ToString
public class CurrentParams {
    private CurrencyPair currencyPair;
    private String period;
    private String chartType;
    private Boolean showAllPairs;
    private Boolean orderRoleFilterEnabled;


}
