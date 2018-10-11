package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.exrates.openapi.models.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("TradeHistoryResponse")
@Data
public class TradeHistoryDto {

    @ApiModelProperty(value = "order id", position = 1, required = true)
    @JsonProperty("order_id")
    private Integer orderId;
    @ApiModelProperty(value = "acceptance date", position = 2, required = true)
    @JsonProperty("date_acceptance")
    private LocalDateTime dateAcceptance;
    @ApiModelProperty(value = "creation date", position = 3, required = true)
    @JsonProperty("date_creation")
    private LocalDateTime dateCreation;
    @ApiModelProperty(value = "amount", position = 4, required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "price", position = 5, required = true)
    private BigDecimal price;
    @ApiModelProperty(value = "total sum", position = 6, required = true)
    private BigDecimal total;
    @ApiModelProperty(value = "commission sum", position = 7, required = true)
    private BigDecimal commission;
    @ApiModelProperty(value = "order type", position = 8, required = true)
    @JsonProperty("order_type")
    private OrderType orderType;
}
