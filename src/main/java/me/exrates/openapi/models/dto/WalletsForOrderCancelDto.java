package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WalletsForOrderCancelDto {

    private int orderId;
    private int orderStatusId;
    private BigDecimal reservedAmount;
    private int walletId;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
}
