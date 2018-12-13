package me.exrates.dao;

import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.dto.onlineTableDto.NotificationDto;

import java.util.List;

/**
 * Created by OLEG on 09.11.2016.
 */
public interface NotificationDao {

    List<Notification> findAllByUser(Integer userId);

    List<NotificationDto> findByUser(Integer userId, Integer offset, Integer limit);

    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    int setReadAllByUser(Integer userId);

    int removeAllByUser(Integer userId);

    List<NotificationOption> getNotificationOptionsByUser(Integer userId);

    void updateNotificationOptions(List<NotificationOption> options);

}
