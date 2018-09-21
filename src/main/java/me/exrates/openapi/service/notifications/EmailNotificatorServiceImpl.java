package me.exrates.openapi.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.exceptions.MessageUndeliweredException;
import me.exrates.openapi.model.Email;
import me.exrates.openapi.model.enums.NotificationTypeEnum;
import me.exrates.openapi.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2(topic = "message_notify")
@Component
public class EmailNotificatorServiceImpl implements NotificatorService {

    @Autowired
    private SendMailService sendMailService;


    @Override
    public Object getSubscriptionByUserId(int userId) {
        return null;
    }

    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        Email email = new Email();
        email.setTo(userEmail);
        email.setMessage(message);
        email.setSubject(subject);
        sendMailService.sendMailMandrill(email);
        return userEmail;
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.EMAIL;
    }
}
