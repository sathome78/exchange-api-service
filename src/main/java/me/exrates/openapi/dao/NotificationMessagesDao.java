package me.exrates.openapi.dao;

import me.exrates.openapi.model.enums.NotificationMessageEventEnum;
import me.exrates.openapi.model.enums.NotificationTypeEnum;

/**
 * Created by Maks on 02.10.2017.
 */
public interface NotificationMessagesDao {

    String gerResourceString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum);

}
