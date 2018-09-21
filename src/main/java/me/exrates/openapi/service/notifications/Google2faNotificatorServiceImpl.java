package me.exrates.openapi.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.exceptions.MessageUndeliweredException;
import me.exrates.openapi.model.enums.NotificationTypeEnum;
import org.springframework.stereotype.Component;


@Log4j2(topic = "message_notify")
@Component
public class Google2faNotificatorServiceImpl implements NotificatorService {

    @Override
    public Object getSubscriptionByUserId(int userId) {
        return null;
    }

    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        return "";
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.GOOGLE2FA;
    }
}
