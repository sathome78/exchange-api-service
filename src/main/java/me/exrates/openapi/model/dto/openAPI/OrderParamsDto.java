package me.exrates.openapi.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter
@ToString
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
