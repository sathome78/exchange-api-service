package me.exrates.openapi.model.enums;

import me.exrates.openapi.exceptions.model.UnsupportedOrderTypeException;

import java.util.Arrays;

public enum OrderBaseType {

    LIMIT(1), STOP_LIMIT(2), ICO(3);


    private int code;

    public int getType() {
        return code;
    }

    OrderBaseType(int code) {
        this.code = code;
    }

    public static OrderBaseType convert(int code) {
        return Arrays.stream(OrderBaseType.values()).filter(ot -> ot.code == code).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderBaseType convert(String name) {
        return Arrays.stream(OrderBaseType.values()).filter(ot -> ot.name().equals(name)).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }
}
