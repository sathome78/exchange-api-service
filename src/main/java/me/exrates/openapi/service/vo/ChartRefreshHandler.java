package me.exrates.openapi.service.vo;

import lombok.EqualsAndHashCode;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by Maks on 04.09.2017.
 */
@EqualsAndHashCode
public class ChartRefreshHandler {

    private int currencyPairId;


    private ChartRefreshHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
    }

}
