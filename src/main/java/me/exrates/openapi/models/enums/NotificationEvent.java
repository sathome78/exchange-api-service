package me.exrates.openapi.models.enums;

import me.exrates.openapi.exceptions.model.UnsupportedNotificationEventException;

public enum NotificationEvent {

    CUSTOM(1), ADMIN(2), ACCOUNT(3), ORDER(4), IN_OUT(5);

    private final int eventType;

    NotificationEvent(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public static NotificationEvent convert(int eventType) {
        switch (eventType) {
            case 1:
                return CUSTOM;
            case 2:
                return ADMIN;
            case 3:
                return ACCOUNT;
            case 4:
                return ORDER;
            case 5:
                return IN_OUT;
            default:
                throw new UnsupportedNotificationEventException("Unsupported notification event");
        }
    }
}