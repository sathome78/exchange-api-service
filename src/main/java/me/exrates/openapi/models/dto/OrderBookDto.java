package me.exrates.openapi.models.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OrderType;

import java.util.List;
import java.util.Map;

@ApiModel("OrderBookResponse")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookDto {

    @ApiModelProperty(value = "order book items by order type", position = 1, required = true)
    private Map<OrderType, List<OrderBookItemDto>> orderBookItems;
}
