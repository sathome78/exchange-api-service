package me.exrates.openapi.models.enums;

import lombok.Getter;

import static me.exrates.openapi.models.enums.OrderBaseType.LIMIT;

@Getter
public enum CurrencyPairType {

    MAIN(LIMIT),
    ICO(OrderBaseType.ICO),
    ALL(null);

    private OrderBaseType orderBaseType;

    CurrencyPairType(OrderBaseType orderBaseType) {
        this.orderBaseType = orderBaseType;
    }
}
