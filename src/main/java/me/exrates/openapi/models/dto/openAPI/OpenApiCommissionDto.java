package me.exrates.openapi.models.dto.openAPI;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.openapi.models.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class OpenApiCommissionDto {

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal input;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal output;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal sell;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal buy;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal transfer;

    public OpenApiCommissionDto(CommissionsDto commissionsDto) {
        this.input = commissionsDto.getInputCommission();
        this.output = commissionsDto.getOutputCommission();
        this.sell = commissionsDto.getSellCommission();
        this.buy = commissionsDto.getBuyCommission();
        this.transfer = commissionsDto.getTransferCommission();
    }
}
