package me.exrates.openapi.service;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.dao.CurrencyDao;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.dto.CurrencyPairLimitDto;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.OrderType;
import me.exrates.openapi.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private UserService userService;

    //+
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyDao.findCurrencyPairById(currencyPairId);
        } catch (EmptyResultDataAccessException e) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    //+
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(Integer currencyPairId, OperationType operationType) {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        OrderType orderType = OrderType.convert(operationType.name());
        return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPairId, userRole.getRole(), orderType.getType());
    }

    //+
    public CurrencyPair getCurrencyPairByName(String pairName) {
        return currencyDao.findCurrencyPairByName(pairName);
    }

    //+
    @Transactional(readOnly = true)
    public CurrencyPair findCurrencyPairIdByName(String pairName) {
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
