package me.exrates.service.vo;

import lombok.EqualsAndHashCode;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Timer;

/**
 * Created by Maks on 05.09.2017.
 */
@EqualsAndHashCode
public class MyTradesHandler {

    private Timer timer;

    private int currencyPairId;


    private MyTradesHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
        timer = new Timer();
       /* timer.schedule(new TimerTask() {
            @Override
            public void run() {
                locksMap.clear();
            }
        }, LOCKS_CLEAR_DELAY);*/
    }

}
