package me.exrates.openapi.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import me.exrates.model.dto.NotificatorTotalPriceDto;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private UserRoleService userRoleService;

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
    public Subscribable getByNotificatorId(int id) {
        Notificator notificator = Optional.ofNullable(this.getById(id))
                .orElseThrow(() -> new RuntimeException(String.valueOf(id)));
        return subscribableMap.get(notificator.getBeanName());
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
    public NotificatorTotalPriceDto getPrices(int notificatorId, int roleId) {
        return notificatorPriceDao.getPrices(notificatorId, roleId);
    }

    @Override
    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        return notificatorPriceDao.getSubscriptionPrice(notificatorId, roleId);
    }

    @Override
    public List<Notificator> getNotificatorSettingsByRole(int roleId) {
        return notificatorsDao.getAdminDtoByRole(roleId);
    }

    public List<Notificator> getAllNotificators() {
        return notificatorsDao.getAllNotificators();
    }

    @Override
    public void setEnable(int notificatorId, boolean enable) {
        notificatorsDao.setEnable(notificatorId, enable);
    }

    @Override
    public void updateNotificatorPrice(BigDecimal price, int roleId, int notificatorId) {
        NotificationTypeEnum typeEnum = NotificationTypeEnum.convert(notificatorId);
        notificatorPriceDao.updatePrice(price, roleId, notificatorId, typeEnum.getPriceColumn());
    }
}
