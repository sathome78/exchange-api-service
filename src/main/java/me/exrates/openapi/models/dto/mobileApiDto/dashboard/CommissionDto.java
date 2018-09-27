package me.exrates.openapi.models.dto.mobileApiDto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDto {

    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal sellCommission;
    private BigDecimal buyCommission;
    private BigDecimal transferCommission;
}
