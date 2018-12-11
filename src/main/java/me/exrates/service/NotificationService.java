package me.exrates.service;

import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.vo.CacheData;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

/**
 * Created by OLEG on 10.11.2016.
 */
public interface NotificationService {

  long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                   Object[] messageArgs);

  long createLocalizedNotification(String userEmail, NotificationEvent cause, String titleCode, String messageCode,
                                   Object[] messageArgs);

  void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                  Object[] messageArgs, Locale locale);

  void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                  Object[] messageArgs);

  void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                  Object[] messageArgs);

  void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                  Object[] messageArgs, Locale locale);

  void notifyUser(Integer userId, NotificationEvent cause, String titleMessage, String message);

  List<Notification> findAllByUser(String email);

  List<NotificationDto> findByUser(String email, CacheData cacheData, Integer offset, Integer limit);

  boolean setRead(Long notificationId);

  boolean remove(Long notificationId);

  int setReadAllByUser(String email);

  int removeAllByUser(String email);

  List<NotificationOption> getNotificationOptionsByUser(Integer userId);

  void updateUserNotifications(List<NotificationOption> options);

}
