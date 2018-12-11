package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import me.exrates.model.dto.OrderCreationResultDto;

import java.math.BigDecimal;

@Getter
@ToString
public class OrderCreationResultOpenApiDto {

    @JsonProperty("created_order_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer createdOrderId;

    @JsonProperty("auto_accepted_quantity")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer autoAcceptedQuantity;

    @JsonProperty("partially_accepted_amount")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal partiallyAcceptedAmount;

    public OrderCreationResultOpenApiDto(OrderCreationResultDto orderCreationResultDto) {
        this.createdOrderId = orderCreationResultDto.getCreatedOrderId();
        this.autoAcceptedQuantity = orderCreationResultDto.getAutoAcceptedQuantity();
        this.partiallyAcceptedAmount = orderCreationResultDto.getPartiallyAcceptedAmount();
    }
}
