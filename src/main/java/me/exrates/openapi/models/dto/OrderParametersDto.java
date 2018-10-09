package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.exrates.openapi.models.enums.OrderType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderParametersDto {

    @NotNull
    @JsonProperty("currency_pair")
    private String pair;
    @NotNull
    @JsonProperty("order_type")
    private OrderType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal price;
}
