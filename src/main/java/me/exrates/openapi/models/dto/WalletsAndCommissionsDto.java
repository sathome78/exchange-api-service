package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WalletsAndCommissionsDto {

    private int userId;
    private int spendWalletId;
    private BigDecimal spendWalletActiveBalance;
    private int commissionId;
    private BigDecimal commissionValue;
}
