package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OrderType;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookItem {

    @JsonIgnore
    private OrderType orderType;
    private BigDecimal amount;
    private BigDecimal rate;
}
