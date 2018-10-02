package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationResultDto {

    @JsonProperty("created_order_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer createdOrderId;
    @JsonProperty("auto_accepted_quantity")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer autoAcceptedQuantity;
    @JsonProperty("partially_accepted")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal partiallyAcceptedAmount;
    @JsonProperty("partially_accepted_order_full_ammount")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal partiallyAcceptedOrderFullAmount;
}
