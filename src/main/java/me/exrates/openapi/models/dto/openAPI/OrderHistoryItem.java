package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.serializer.BigDecimalToDoubleSerializer;
import me.exrates.openapi.models.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrderHistoryItem {

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("date_acceptance")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateAcceptance;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal total;
    @JsonProperty("order_type")
    private OrderType orderType;
}