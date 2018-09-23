package me.exrates.openapi.service.events;

import me.exrates.openapi.model.ExOrder;

public class CreateOrderEvent extends OrderEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CreateOrderEvent(ExOrder source) {
        super(source);
    }
}
