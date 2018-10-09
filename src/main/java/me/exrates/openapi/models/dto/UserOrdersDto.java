package me.exrates.openapi.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserOrdersDto {

    private Integer id;
    @JsonProperty("currency_pair")
    private String currencyPair;
    private BigDecimal amount;
    @JsonProperty("order_type")
    private String orderType;
    private BigDecimal price;
    @JsonProperty("date_created")
    private LocalDateTime dateCreation;
    @JsonProperty("date_accepted")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dateAcceptance;
}
