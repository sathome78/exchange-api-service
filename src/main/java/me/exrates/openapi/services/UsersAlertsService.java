package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.models.dto.AlertDto;
import me.exrates.openapi.models.enums.AlertType;
import me.exrates.openapi.repositories.UserAlertsRepository;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UsersAlertsService {

    private final UserAlertsRepository userAlertsRepository;

    @Autowired
    public UsersAlertsService(UserAlertsRepository userAlertsRepository) {
        this.userAlertsRepository = userAlertsRepository;
    }

    @PostConstruct
    private void init() {
        AlertType alertType = AlertType.UPDATE;
        AlertDto alertDto = getAlertByType(AlertType.UPDATE);
        if (alertDto.isEnabled() && alertDto.getEventStart().isBefore(LocalDateTime.now())) {
            if (disableAlert(alertType)) {
                log.debug("Alert disabled");
            }
        }
    }

    @Loggable(caption = "Get alert by type")
    @Transactional(readOnly = true)
    public AlertDto getAlertByType(AlertType alertType) {
        return userAlertsRepository.getAlert(alertType.name());
    }

    @Loggable(caption = "Disable alert", logLevel = Level.DEBUG)
    private boolean disableAlert(AlertType alertType) {
        AlertDto alertDto = AlertDto.builder()
                .alertType(alertType.name())
                .lenghtOfWorks(null)
                .eventStart(null)
                .launchDateTime(null)
                .enabled(false)
                .build();

        return userAlertsRepository.updateAlert(alertDto);
    }
}
