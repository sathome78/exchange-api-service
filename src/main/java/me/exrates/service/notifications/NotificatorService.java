package me.exrates.service.notifications;

import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.exception.MessageUndeliweredException;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificatorService {


    Object getSubscriptionByUserId(int userId);

    String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException;

    NotificationTypeEnum getNotificationType();

}
