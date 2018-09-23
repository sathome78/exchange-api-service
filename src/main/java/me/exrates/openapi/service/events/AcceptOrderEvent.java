package me.exrates.openapi.service.events;

import me.exrates.openapi.model.ExOrder;

public class AcceptOrderEvent extends OrderEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AcceptOrderEvent(ExOrder source) {
        super(source);
    }
}
