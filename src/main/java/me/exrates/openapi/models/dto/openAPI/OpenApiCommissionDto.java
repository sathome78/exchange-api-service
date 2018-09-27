package me.exrates.openapi.models.dto.openAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionDto;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class OpenApiCommissionDto {

    private BigDecimal input;
    private BigDecimal output;
    private BigDecimal sell;
    private BigDecimal buy;
    private BigDecimal transfer;

    public OpenApiCommissionDto(CommissionDto commissionDto) {
        this.input = commissionDto.getInputCommission();
        this.output = commissionDto.getOutputCommission();
        this.sell = commissionDto.getSellCommission();
        this.buy = commissionDto.getBuyCommission();
        this.transfer = commissionDto.getTransferCommission();
    }
}
