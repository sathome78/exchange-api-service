package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDto {

    private int orderId;
    private int userId;
    private OrderStatus status;

//  these fields will be transferred to blank creation form

    private CurrencyPair currencyPair;
    private int comissionForBuyId;
    private BigDecimal comissionForBuyRate;
    private int comissionForSellId;
    private BigDecimal comissionForSellRate;
    private int walletIdCurrencyBase;
    private BigDecimal currencyBaseBalance;
    private int walletIdCurrencyConvert;
    private BigDecimal currencyConvertBalance;

//  these fields will be returned from creation form after submitting*/
//  IMPORTANT: operationType is not populated because OrderCreateDto is used for page the orders,
//  that consists two forms: for BUY and for SELL. After submit this field will be set because we submit concrete form: BUY or SELL.
//  However if we transfered to form the orders from dashboard, the fields one form (of two forms: SELL or BUY) must be filled.
//  To determine which of these forms to be filled, we must set field operationType

    private BigDecimal stop; //stop rate for stop order
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount; //amount of base currency: base currency can be bought or sold dependending on operationType
    private OrderBaseType orderBaseType;

//  these fields will be calculated after submitting the order and before final creation confirmation the order
//  (here: OrderController.submitNewOrderToSell())
//  These amounts calculated directly in java (after check the order parameters in java validator) and will be persistented in db
//  (before this step these amounts were being calculated by javascript and may be occur some difference)

    private BigDecimal spentWalletBalance;
    private BigDecimal spentAmount;
    private BigDecimal total; //calculated amount of currency conversion = amount * exchangeRate
    private int commissionId;
    private BigDecimal commission; //calculated commission amount depending on operationType and corresponding commission rate
    private BigDecimal totalWithComission; //total + commission
    private Integer sourceId;

    public OrderCreateDto calculateAmounts() {
        switch (operationType) {
            case SELL:
                this.spentWalletBalance = isNull(this.currencyBaseBalance) ? BigDecimal.ZERO : this.currencyBaseBalance;
                this.total = BigDecimalProcessingUtil.doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
                this.commissionId = this.comissionForSellId;
                this.commission = BigDecimalProcessingUtil.doAction(this.total, this.comissionForSellRate, ActionType.MULTIPLY_PERCENT);
                this.totalWithComission = BigDecimalProcessingUtil.doAction(this.total, this.commission.negate(), ActionType.ADD);
                this.spentAmount = this.amount;
                return this;
            case BUY:
                this.spentWalletBalance = isNull(this.currencyConvertBalance) ? BigDecimal.ZERO : this.currencyConvertBalance;
                this.total = BigDecimalProcessingUtil.doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
                this.commissionId = this.comissionForBuyId;
                this.commission = BigDecimalProcessingUtil.doAction(this.total, this.comissionForBuyRate, ActionType.MULTIPLY_PERCENT);
                this.totalWithComission = BigDecimalProcessingUtil.doAction(this.total, this.commission, ActionType.ADD);
                this.spentAmount = BigDecimalProcessingUtil.doAction(this.total, this.commission, ActionType.ADD);
                return this;
            default:
                return this;
        }
    }
}
