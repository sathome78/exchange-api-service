package me.exrates.openapi.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.exrates.openapi.exceptions.model.UnsupportedOrderTypeException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

@Getter
@AllArgsConstructor
public enum OrderType {

    SELL(1, OperationType.SELL, Comparator.naturalOrder()),
    BUY(2, OperationType.BUY, Comparator.reverseOrder());

    private int type;
    private OperationType operationType;
    // need for sorting orders by rate: DESC for BUY, ASC for SELL
    private Comparator<BigDecimal> benefitRateComparator;

    public static OrderType convert(int type) {
        return Arrays.stream(OrderType.values())
                .filter(ot -> ot.type == type)
                .findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderType convert(String name) {
        return Arrays.stream(OrderType.values())
                .filter(ot -> ot.name().equals(name))
                .findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderType fromOperationType(OperationType operationType) {
        return Arrays.stream(OrderType.values())
                .filter(item -> item.operationType == operationType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Operation type: %s not convertible to order type", operationType.name())));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
