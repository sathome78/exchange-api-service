package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationMessagesDao;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "message_notify")
@Component
public class NotificationMessageServiceImpl implements NotificationMessageService {

    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private NotificationMessagesDao notificationMessagesDao;


    private NotificationResultDto getResponseString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum, String contactToNotify) {
        String message = notificationMessagesDao.gerResourceString(event, typeEnum);
        return new NotificationResultDto(message, new String[]{contactToNotify});
    }


}
