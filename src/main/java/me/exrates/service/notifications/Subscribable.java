package me.exrates.service.notifications;

import me.exrates.model.dto.NotificatorSubscription;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Maks on 02.10.2017.
 */
public interface Subscribable {

    Object subscribe(Object subscriptionObject);

}
