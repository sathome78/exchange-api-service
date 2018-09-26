package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.exrates.openapi.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeHistoryDto {

    @JsonProperty("order_id")
    private Integer orderId;
    @JsonProperty("date_acceptance")
    private LocalDateTime dateAcceptance;
    @JsonProperty("date_creation")
    private LocalDateTime dateCreation;
    private BigDecimal amount;
    private BigDecimal price;
    private BigDecimal total;
    private BigDecimal commission;
    @JsonProperty("order_type")
    private OrderType orderType;
}
