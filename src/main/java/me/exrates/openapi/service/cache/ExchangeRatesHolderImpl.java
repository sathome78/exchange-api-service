package me.exrates.openapi.service.cache;

import me.exrates.openapi.dao.OrderDao;
import me.exrates.openapi.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    @Autowired
    public ExchangeRatesHolderImpl() {

    }



}
