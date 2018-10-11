package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.CurrencyPairInfo;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Loggable(caption = "Get active currency pairs")
    @Transactional(readOnly = true)
    public List<CurrencyPairInfo> getActiveCurrencyPairs() {
        return currencyRepository.findActiveCurrencyPairs();
    }

    @Loggable(caption = "Get currency pair by name")
    @Transactional(readOnly = true)
    public CurrencyPair getCurrencyPairByName(String pairName) {
        log.debug("Try to find currency pair by name: {}", pairName);
        CurrencyPair currencyPair = currencyRepository.findCurrencyPairByName(pairName);
        if (isNull(currencyPair)) {
            throw new CurrencyPairNotFoundException(String.format("Currency pair with name: %s not found", pairName));
        }
        log.debug("Currency pair found");
        return currencyPair;
    }

    @Loggable(caption = "Get currency pair by id")
    @Transactional(readOnly = true)
    public CurrencyPair getCurrencyPairById(int currencyPairId) {
        log.debug("Try to find currency pair by id: {}", currencyPairId);

        CurrencyPair currencyPair = currencyRepository.findCurrencyPairById(currencyPairId);
        if (isNull(currencyPair)) {
            throw new CurrencyPairNotFoundException(String.format("Currency pair with id: %s not found", currencyPairId));
        }
        log.debug("Currency pair found");
        return currencyPair;
    }

    @Loggable(caption = "Get currency pair limit by user role")
    @Transactional(readOnly = true)
    public CurrencyPairLimitDto getLimitByRole(CurrencyPair currencyPair,
                                               OperationType operationType,
                                               UserRole userRole) {
        OrderType orderType = OrderType.convert(operationType.getType());

        return currencyRepository.findCurrencyPairLimitForRoleByPairAndType(currencyPair.getId(), userRole.getRole(), orderType.getType());
    }

    @Loggable(caption = "Find currency pair by name")
    @Transactional(readOnly = true)
    public Integer findCurrencyPairIdByName(String pairName) {
        log.debug("Try to find currency pair by name: {}", pairName);

        Integer currencyPairId = currencyRepository.findActiveCurrencyPairIdByName(pairName);
        if (isNull(currencyPairId)) {
            throw new CurrencyPairNotFoundException(String.format("Currency pair with name: %s not found", pairName));
        }
        log.debug("Currency pair found");
        return currencyPairId;
    }
}
