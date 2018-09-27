package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.CurrencyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class CurrencyService {

    private final CurrencyDao currencyDao;

    @Autowired
    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    //+
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyDao.findCurrencyPairById(currencyPairId);
        } catch (EmptyResultDataAccessException e) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    //+
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(CurrencyPair currencyPair,
                                                                      OperationType operationType,
                                                                      UserRole userRole) {
        OrderType orderType = OrderType.convert(operationType.getType());

        return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPair.getId(), userRole.getRole(), orderType.getType());
    }

    //+
    public CurrencyPair getCurrencyPairByName(String pairName) {
        return currencyDao.findCurrencyPairByName(pairName);
    }

    //+
    @Transactional(readOnly = true)
    public CurrencyPair findCurrencyPairByName(String pairName) {
        log.debug("Try to find currency pair with name: {}", pairName);
        CurrencyPair currencyPair = currencyDao.findActiveCurrencyPairByName(pairName);
        if (isNull(currencyPair)) {
            throw new CurrencyPairNotFoundException(String.format("Currency pair with name: %s not found", pairName));
        }
        log.debug("Currency pair found");
        return currencyPair;
    }

    //+
    @Transactional(readOnly = true)
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return currencyDao.findActiveCurrencyPairs();
    }
}
