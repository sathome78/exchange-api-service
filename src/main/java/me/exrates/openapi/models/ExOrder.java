package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.openapi.models.dto.OrderCreateDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"currencyPair"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class ExOrder {

    private int id;
    private int userId;
    private int currencyPairId;
    private OperationType operationType;
    private BigDecimal exRate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private int comissionId;
    private BigDecimal commissionFixedAmount;
    private int userAcceptorId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAcception;
    private OrderStatus status;
    private CurrencyPair currencyPair;
    private Integer sourceId;
    private BigDecimal stop;
    private OrderBaseType orderBaseType = OrderBaseType.LIMIT;

    public ExOrder(OrderCreateDto orderCreateDto) {
        this.id = orderCreateDto.getOrderId();
        this.userId = orderCreateDto.getUserId();
        this.currencyPairId = orderCreateDto.getCurrencyPair().getId();
        this.operationType = orderCreateDto.getOperationType();
        this.exRate = orderCreateDto.getExchangeRate();
        this.amountBase = orderCreateDto.getAmount();
        this.amountConvert = orderCreateDto.getTotal();
        this.comissionId = orderCreateDto.getCommissionId();
        this.commissionFixedAmount = orderCreateDto.getCommission();
        this.status = orderCreateDto.getStatus();
        this.currencyPair = orderCreateDto.getCurrencyPair();
        this.sourceId = orderCreateDto.getSourceId();
        this.stop = orderCreateDto.getStop();
        this.orderBaseType = orderCreateDto.getOrderBaseType();
    }
}
