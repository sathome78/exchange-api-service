package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class OpenOrderDto {

    private Integer id;
    @JsonProperty("order_type")
    private String orderType;
    private BigDecimal amount;
    private BigDecimal price;

}
