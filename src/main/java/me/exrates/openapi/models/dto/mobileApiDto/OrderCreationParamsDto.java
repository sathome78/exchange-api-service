package me.exrates.openapi.models.dto.mobileApiDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OperationType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationParamsDto {

    @NotNull
    private Integer currencyPairId;
    @NotNull
    private OperationType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal rate;
}
