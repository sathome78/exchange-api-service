package me.exrates.openapi.components;

import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.dto.OrderCreateDto;
import me.exrates.openapi.models.enums.CurrencyPairType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.OrderRepository;
import me.exrates.openapi.services.CurrencyService;
import me.exrates.openapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class OrderValidator {

    private final UserService userService;
    private final CurrencyService currencyService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderValidator(UserService userService,
                          CurrencyService currencyService,
                          OrderRepository orderRepository) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.orderRepository = orderRepository;
    }

    public boolean validate(OrderCreateDto orderCreateDto) {
        if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        final CurrencyPair currencyPair = orderCreateDto.getCurrencyPair();
        final OperationType operationType = orderCreateDto.getOperationType();
        final UserRole userRole = userService.getUserRoleFromSecurityContext();

        CurrencyPairLimitDto currencyPairLimit = currencyService.getLimitByRole(currencyPair, operationType, userRole);

        if (nonNull(orderCreateDto.getOrderBaseType()) && orderCreateDto.getOrderBaseType() == OrderBaseType.STOP_LIMIT) {
            if (isNull(orderCreateDto.getStop()) || orderCreateDto.getStop().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            } else {
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMinRate()) < 0) {
                    return false;
                }
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                    return false;
                }
            }
        }
        if (orderCreateDto.getCurrencyPair().getPairType() == CurrencyPairType.ICO) {
            if (nonNull(orderCreateDto.getOrderBaseType()) && orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
                throw new RuntimeException("Unsupported type of order");
            }
            if (orderCreateDto.getOperationType() == OperationType.SELL) {
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream()
                        .filter(grantedAuthority -> grantedAuthority.getAuthority().equals(UserRole.ICO_MARKET_MAKER.name()))
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("User role not allowed"));
            }
            if (orderCreateDto.getOperationType() == OperationType.BUY) {
                BigDecimal lastRate = orderRepository.getLowestOpenOrderPriceByCurrencyPairAndOperationType(
                        orderCreateDto.getCurrencyPair().getId(),
                        OperationType.SELL.getType());
                if (isNull(lastRate) || orderCreateDto.getExchangeRate().compareTo(lastRate) < 0) {
                    return false;
                }
            }
        }
        if (nonNull(orderCreateDto.getAmount())) {
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMaxAmount()) > 0) {
                return false;
            }
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMinAmount()) < 0) {
                return false;
            }
        }
        if (nonNull(orderCreateDto.getExchangeRate())) {
            if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) < 1) {
                return false;
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMinRate()) < 0) {
                return false;
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                return false;
            }

        }
        if (nonNull(orderCreateDto.getAmount()) && nonNull(orderCreateDto.getExchangeRate())) {
            return orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0
                    && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
        }
        return true;
    }
}
