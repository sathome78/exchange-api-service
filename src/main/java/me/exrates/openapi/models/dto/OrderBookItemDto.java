package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OrderType;

import java.math.BigDecimal;

@ApiModel("OrderBookItemResponse")
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookItemDto {

    @ApiModelProperty(value = "order type", position = 1, hidden = true, required = true)
    @JsonIgnore
    private OrderType orderType;
    @ApiModelProperty(value = "amount", position = 2, required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "rate", position = 3, required = true)
    private BigDecimal rate;
}
