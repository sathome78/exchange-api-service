package me.exrates.openapi.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import me.exrates.model.dto.NotificatorTotalPriceDto;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.openapi.dao.NotificatorDao;
import me.exrates.openapi.dao.NotificatorPriceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2(topic = "message_notify")
@Service
public class NotificatorsService {

    @Autowired
    private NotificatorDao notificatorsDao;
    @Autowired
    private NotificatorPriceDao notificatorPriceDao;

    @Autowired
    Map<String, NotificatorService> notificatorsMap;
    @Autowired
    Map<String, Subscribable> subscribableMap;
    @Autowired
    private UserRoleService userRoleService;

    public NotificatorService getNotificationService(Integer notificatorId) {
        Notificator notificator = Optional.ofNullable(this.getById(notificatorId))
                .orElseThrow(() -> new RuntimeException(String.valueOf(notificatorId)));
        return notificatorsMap.get(notificator.getBeanName());
    }

    public Map<Integer, Object> getSubscriptions(int userId) {
        Map<Integer, Object> subscrMap = new HashMap<>();
        Arrays.asList(NotificationTypeEnum.values()).forEach(p->{
            if (p.isNeedSubscribe()) {
                NotificatorService service = this.getNotificationService(p.getCode());
                subscrMap.put(p.getCode(), service.getSubscriptionByUserId(userId));
            }
        });
        return subscrMap;
    }

    @Override
    public Notificator getById(int id){
        return notificatorsDao.getById(id);
    }

    public List<Notificator> getAllNotificators() {
        return notificatorsDao.getAllNotificators();
    }
}
