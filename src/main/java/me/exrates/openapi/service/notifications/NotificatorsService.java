package me.exrates.openapi.service.notifications;

import me.exrates.model.dto.Notificator;
import me.exrates.model.dto.NotificatorTotalPriceDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Maks on 06.10.2017.
 */
public interface NotificatorsService {

    NotificatorService getNotificationService(Integer notificatorId);

    NotificatorService getNotificationServiceByBeanName(String beanName);

    Map<Integer, Object> getSubscriptions(int userId);

    Subscribable getByNotificatorId(int id);

    Notificator getById(int id);

    BigDecimal getMessagePrice(int notificatorId, int roleId);

    NotificatorTotalPriceDto getPrices(int notificatorId, int roleId);


    BigDecimal getSubscriptionPrice(int notificatorId, int roleId);

    List<Notificator> getNotificatorSettingsByRole(int roleId);

    void setEnable(int notificatorId, boolean enable);

    void updateNotificatorPrice(BigDecimal price, int roleId, int notificatorId);

    List<Notificator> getAllNotificators();

}
