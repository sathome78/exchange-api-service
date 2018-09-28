package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.exrates.openapi.models.enums.OrderType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderParamsDto {

    @NotNull
    @JsonProperty("currency_pair")
    private String currencyPair;
    @NotNull
    @JsonProperty("order_type")
    private OrderType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal price;
}
