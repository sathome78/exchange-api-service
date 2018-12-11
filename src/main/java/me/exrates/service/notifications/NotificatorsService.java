package me.exrates.service.notifications;

import me.exrates.model.dto.Notificator;

import java.math.BigDecimal;

/**
 * Created by Maks on 06.10.2017.
 */
public interface NotificatorsService {

    NotificatorService getNotificationService(Integer notificatorId);

    NotificatorService getNotificationServiceByBeanName(String beanName);

    Notificator getById(int id);

    BigDecimal getMessagePrice(int notificatorId, int roleId);


    BigDecimal getSubscriptionPrice(int notificatorId, int roleId);

}
