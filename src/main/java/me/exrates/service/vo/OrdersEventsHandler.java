package me.exrates.service.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Timer;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@EqualsAndHashCode
@Getter
@Setter
public class OrdersEventsHandler {

    private Integer pairId;
    private OperationType operationType;

    private static final long MIN_REFRESH_TIME = 600;
    private static final long MAX_REFRESH_TIME = 1000;

    private Timer timer;

    private static final int MAX_EVENTS = 3;
    private static final int MIN_EVENTS = 1;
    private int lastEventsCountBeforeSend;


    private OrdersEventsHandler(Integer pairId, OperationType operationType) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.pairId = pairId;
        this.operationType = operationType;
        timer = new Timer();
    }


}
