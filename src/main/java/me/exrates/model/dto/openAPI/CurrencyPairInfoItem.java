package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CurrencyPairInfoItem {

    private String name;

    @JsonProperty("url_symbol")
    private String urlSymbol;

    public CurrencyPairInfoItem(String name) {
        this.name = name;
        this.urlSymbol = name.replace('/', '_').toLowerCase();
    }
}
