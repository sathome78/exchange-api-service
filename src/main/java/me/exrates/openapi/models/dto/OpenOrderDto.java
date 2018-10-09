package me.exrates.openapi.models.dto;

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
public class OpenOrderDto {

    private Integer id;
    @JsonProperty("order_type")
    private String orderType;
    private BigDecimal amount;
    private BigDecimal price;

}
