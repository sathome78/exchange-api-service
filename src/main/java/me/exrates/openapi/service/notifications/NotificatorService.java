package me.exrates.openapi.service.notifications;

import me.exrates.openapi.exceptions.MessageUndeliweredException;
import me.exrates.openapi.model.enums.NotificationTypeEnum;

public interface NotificatorService {

    Object getSubscriptionByUserId(int userId);

    String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException;

    NotificationTypeEnum getNotificationType();

}
