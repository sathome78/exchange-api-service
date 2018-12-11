package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;

/**
 * Created by Maks on 08.10.2017.
 */
public interface NotificationsSettingsService {

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);
/*
    void createOrUpdate(NotificationsUserSetting setting);

    Object get2faOptionsForUser(int id);

    Map<Integer, NotificationsUserSetting> getSettingsMap(int userId);*/
}
