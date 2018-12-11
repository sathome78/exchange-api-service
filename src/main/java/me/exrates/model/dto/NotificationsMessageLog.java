package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.NotificationMessageEventEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Maks on 29.09.2017.
 */
@Data
public class NotificationsMessageLog {

    private int id;
    private int userId;
    private LocalDateTime localDateTime;
    private BigDecimal paySum;
    private NotificationMessageEventEnum event;
}
