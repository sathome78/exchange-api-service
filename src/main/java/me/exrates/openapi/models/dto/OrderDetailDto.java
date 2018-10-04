package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderStatus;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    private int orderId;
    private OrderStatus orderStatus;
    private BigDecimal orderCreatorReservedAmount;
    private int orderCreatorReservedWalletId;
    private int transactionId;
    private OperationType transactionType;
    private BigDecimal transactionAmount;
    private int userWalletId;
    private int companyWalletId;
    private BigDecimal companyCommission;
}

