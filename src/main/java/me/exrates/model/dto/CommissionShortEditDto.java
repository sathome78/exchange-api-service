package me.exrates.model.dto;

import lombok.ToString;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by OLEG on 30.01.2017.
 */
@ToString
public class CommissionShortEditDto {
    private OperationType operationType;
    private String operationTypeLocalized;
    private BigDecimal value;

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getOperationTypeLocalized() {
        return operationTypeLocalized;
    }

    public void setOperationTypeLocalized(String operationTypeLocalized) {
        this.operationTypeLocalized = operationTypeLocalized;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
