package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.dto.AlertDto;
import me.exrates.openapi.models.enums.AlertType;
import me.exrates.openapi.repositories.UserAlertsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UsersAlertsService {

    private final UserAlertsDao userAlertsDao;

    @Autowired
    public UsersAlertsService(UserAlertsDao userAlertsDao) {
        this.userAlertsDao = userAlertsDao;
    }

    @PostConstruct
    private void init() {
        AlertType alertType = AlertType.UPDATE;
        AlertDto alertDto = getAlert(AlertType.UPDATE);
        if (alertDto.isEnabled() && alertDto.getEventStart().isBefore(LocalDateTime.now())) {
            if (disableAlert(alertType)) {
                log.debug("Alert disabled");
            }
        }
    }

    @Transactional(readOnly = true)
    public AlertDto getAlert(AlertType alertType) {
        return userAlertsDao.getAlert(alertType.name());
    }

    private boolean disableAlert(AlertType alertType) {
        AlertDto alertDto = AlertDto.builder()
                .alertType(alertType.name())
                .lenghtOfWorks(null)
                .eventStart(null)
                .launchDateTime(null)
                .enabled(false)
                .build();

        return userAlertsDao.updateAlert(alertDto);
    }
}
