package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Maks on 06.10.2017.
 */
@Log4j2(topic = "message_notify")
@Service
public class NotificatorsServiceImpl implements NotificatorsService {


    @Autowired
    private NotificatorsDao notificatorsDao;
    @Autowired
    private NotificatorPriceDao notificatorPriceDao;
    @Autowired
    Map<String, NotificatorService> notificatorsMap;
    @Autowired
    Map<String, Subscribable> subscribableMap;

    @Override
    public NotificatorService getNotificationService(Integer notificatorId) {
        Notificator notificator = Optional.ofNullable(this.getById(notificatorId))
                .orElseThrow(() -> new RuntimeException(String.valueOf(notificatorId)));
        return notificatorsMap.get(notificator.getBeanName());
    }

    @Override
    public NotificatorService getNotificationServiceByBeanName(String beanName) {
        return notificatorsMap.get(beanName);
    }

    @Override
    public Notificator getById(int id){
        return notificatorsDao.getById(id);
    }

    @Override
    public BigDecimal getMessagePrice(int notificatorId, int roleId) {
        return notificatorPriceDao.getFeeMessagePrice(notificatorId, roleId);
    }

    @Override
    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        return notificatorPriceDao.getSubscriptionPrice(notificatorId, roleId);
    }

}
