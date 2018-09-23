package me.exrates.openapi.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.NotificatorPriceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Log4j2(topic = "message_notify")
@Service
public class NotificatorsService {

    @Autowired
    private NotificatorPriceDao notificatorPriceDao;

    public BigDecimal getMessagePrice(int notificatorId, int roleId) {
        return notificatorPriceDao.getFeeMessagePrice(notificatorId, roleId);
    }

    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        return notificatorPriceDao.getSubscriptionPrice(notificatorId, roleId);
    }
}
