package me.exrates.openapi.services;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.repositories.UserAlertsDao;
import me.exrates.openapi.models.dto.AlertDto;
import me.exrates.openapi.models.enums.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Log4j2
@Service
public class UsersAlertsService {

    @Autowired
    private UserAlertsDao userAlertsDao;

    @PostConstruct
    private void init() {
        AlertType alertType = AlertType.UPDATE;
        AlertDto alertDto = getAlert(AlertType.UPDATE);
        if (alertDto.isEnabled() && alertDto.getEventStart().isBefore(LocalDateTime.now())) {
            disableAlert(alertType);
        }
    }

    @Transactional
    public AlertDto getAlert(AlertType alertType) {
        return userAlertsDao.getAlert(alertType.name());
    }

    private void disableAlert(AlertType alertType) {
        userAlertsDao.updateAlert(AlertDto
                .builder()
                .alertType(alertType.name())
                .lenghtOfWorks(null)
                .eventStart(null)
                .launchDateTime(null)
                .enabled(false)
                .build());
    }
}
