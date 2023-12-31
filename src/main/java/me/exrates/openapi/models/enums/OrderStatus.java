package me.exrates.openapi.models.enums;

import lombok.Getter;
import me.exrates.openapi.exceptions.model.UnsupportedOrderStatusException;

import java.util.stream.Stream;

@Getter
public enum OrderStatus {

    INPROCESS(1),
    OPENED(2),
    CLOSED(3),
    CANCELLED(4),
    DELETED(5),
    DRAFT(6),
    SPLIT_CLOSED(7);

    private int status;

    OrderStatus(int status) {
        this.status = status;
    }

    public static OrderStatus convert(int id) {
        return Stream.of(OrderStatus.values())
                .filter(item -> item.getStatus() == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedOrderStatusException(id));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
