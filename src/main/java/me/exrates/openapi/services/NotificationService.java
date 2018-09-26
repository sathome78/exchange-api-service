package me.exrates.openapi.services;

import me.exrates.openapi.models.enums.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private UserService userService;

    //+
    public long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
        Locale locale = new Locale(userService.getPreferedLang(userId));
        return 0L /*createNotification(userId, messageSource.getMessage(titleCode, null, locale),
                messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale), cause)*/;
    }
}
