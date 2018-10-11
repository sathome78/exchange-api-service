package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@ApiModel("CurrencyPairResponse")
@Getter
@ToString
public class CurrencyPairInfo {

    @ApiModelProperty(value = "currency pair name", position = 1, required = true)
    private String name;
    @ApiModelProperty(value = "currency pair symbol", position = 2, required = true)
    @JsonProperty("url_symbol")
    private String urlSymbol;

    public CurrencyPairInfo(String name) {
        this.name = name;
        this.urlSymbol = name.replace('/', '_').toLowerCase();
    }
}
