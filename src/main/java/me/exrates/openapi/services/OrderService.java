package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.components.TransactionDescription;
import me.exrates.openapi.exceptions.AlreadyAcceptedOrderException;
import me.exrates.openapi.exceptions.AttemptToAcceptBotOrderException;
import me.exrates.openapi.exceptions.IncorrectCurrentUserException;
import me.exrates.openapi.exceptions.InsufficientCostsForAcceptionException;
import me.exrates.openapi.exceptions.NotCreatableOrderException;
import me.exrates.openapi.exceptions.NotEnoughUserWalletMoneyException;
import me.exrates.openapi.exceptions.OrderAcceptionException;
import me.exrates.openapi.exceptions.OrderCancellingException;
import me.exrates.openapi.exceptions.OrderCreationException;
import me.exrates.openapi.exceptions.OrderDeletingException;
import me.exrates.openapi.exceptions.WalletCreationException;
import me.exrates.openapi.exceptions.api.OrderParamsWrongException;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.UserRoleSettings;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.dto.CandleChartItemDto;
import me.exrates.openapi.models.dto.CoinmarketApiDto;
import me.exrates.openapi.models.dto.CurrencyPairLimitDto;
import me.exrates.openapi.models.dto.OrderCreateDto;
import me.exrates.openapi.models.dto.OrderCreationResultDto;
import me.exrates.openapi.models.dto.OrderDetailDto;
import me.exrates.openapi.models.dto.OrderValidationDto;
import me.exrates.openapi.models.dto.TradeHistoryDto;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.dto.UserTradeHistoryDto;
import me.exrates.openapi.models.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.openapi.models.dto.WalletsForOrderAcceptionDto;
import me.exrates.openapi.models.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.models.dto.mobileApiDto.OrderCreationParamsDto;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionDto;
import me.exrates.openapi.models.dto.openAPI.OpenOrderDto;
import me.exrates.openapi.models.dto.openAPI.OrderBookItem;
import me.exrates.openapi.models.dto.openAPI.UserOrdersDto;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.CurrencyPairType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.OrderActionEnum;
import me.exrates.openapi.models.enums.OrderBaseType;
import me.exrates.openapi.models.enums.OrderDeleteStatus;
import me.exrates.openapi.models.enums.OrderStatus;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.models.enums.ReferralTransactionStatusEnum;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.TransactionStatus;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.models.enums.WalletTransferStatus;
import me.exrates.openapi.models.vo.BackDealInterval;
import me.exrates.openapi.models.vo.ProfileData;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.CommissionDao;
import me.exrates.openapi.repositories.OrderDao;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static me.exrates.openapi.models.enums.OrderActionEnum.ACCEPT;
import static me.exrates.openapi.models.enums.OrderActionEnum.ACCEPTED;
import static me.exrates.openapi.models.enums.OrderActionEnum.CANCEL;
import static me.exrates.openapi.models.enums.OrderActionEnum.CREATE;
import static me.exrates.openapi.models.enums.OrderActionEnum.DELETE_SPLIT;
import static me.exrates.openapi.utils.CollectionUtil.isNotEmpty;

@Slf4j
@Service
public class OrderService {

    private List<CoinmarketApiDto> coinmarketCachedData = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService coinmarketScheduler = Executors.newSingleThreadScheduledExecutor();

    private final Object autoAcceptLock = new Object();
    private final Object restOrderCreationLock = new Object();

    private final OrderDao orderDao;
    private final CommissionDao commissionDao;
    private final TransactionService transactionService;
    private final UserService userService;
    private final WalletService walletService;
    private final CompanyWalletService companyWalletService;
    private final CurrencyService currencyService;
    private final MessageSource messageSource;
    private final ReferralService referralService;
    private final TransactionDescription transactionDescription;
    private final StopOrderService stopOrderService;
    private final UserRoleService userRoleService;

    @Autowired
    public OrderService(OrderDao orderDao,
                        CommissionDao commissionDao,
                        TransactionService transactionService,
                        UserService userService,
                        WalletService walletService,
                        CompanyWalletService companyWalletService,
                        CurrencyService currencyService,
                        MessageSource messageSource,
                        ReferralService referralService,
                        TransactionDescription transactionDescription,
                        StopOrderService stopOrderService,
                        UserRoleService userRoleService) {
        this.orderDao = orderDao;
        this.commissionDao = commissionDao;
        this.transactionService = transactionService;
        this.userService = userService;
        this.walletService = walletService;
        this.companyWalletService = companyWalletService;
        this.currencyService = currencyService;
        this.messageSource = messageSource;
        this.referralService = referralService;
        this.transactionDescription = transactionDescription;
        this.stopOrderService = stopOrderService;
        this.userRoleService = userRoleService;
    }

    @PostConstruct
    public void init() {
        coinmarketScheduler.scheduleAtFixedRate(() -> {
            List<CoinmarketApiDto> newData = getCoinmarketDataForActivePairs(null);
            coinmarketCachedData = new CopyOnWriteArrayList<>(newData);
        }, 0, 30, TimeUnit.MINUTES);
    }

    //+
    public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, OrderBaseType baseType) {
        return prepareNewOrder(activeCurrencyPair, orderType, userEmail, amount, rate, null, baseType);
    }

    //+
    public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, Integer sourceId, OrderBaseType baseType) {
        Currency spendCurrency = null;
        if (orderType == OperationType.SELL) {
            spendCurrency = activeCurrencyPair.getCurrency1();
        } else if (orderType == OperationType.BUY) {
            spendCurrency = activeCurrencyPair.getCurrency2();
        }
        WalletsAndCommissionsForOrderCreationDto walletsAndCommissions = getWalletAndCommission(userEmail, spendCurrency, orderType);
        /**/
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOperationType(orderType);
        orderCreateDto.setCurrencyPair(activeCurrencyPair);
        orderCreateDto.setAmount(amount);
        orderCreateDto.setExchangeRate(rate);
        orderCreateDto.setUserId(walletsAndCommissions.getUserId());
        orderCreateDto.setCurrencyPair(activeCurrencyPair);
        orderCreateDto.setSourceId(sourceId);
        orderCreateDto.setOrderBaseType(baseType);
        /*todo get 0 comission values from db*/
        if (baseType == OrderBaseType.ICO) {
            walletsAndCommissions.setCommissionValue(BigDecimal.ZERO);
            walletsAndCommissions.setCommissionId(24);
        }
        if (orderType == OperationType.SELL) {
            orderCreateDto.setWalletIdCurrencyBase(walletsAndCommissions.getSpendWalletId());
            orderCreateDto.setCurrencyBaseBalance(walletsAndCommissions.getSpendWalletActiveBalance());
            orderCreateDto.setComissionForSellId(walletsAndCommissions.getCommissionId());
            orderCreateDto.setComissionForSellRate(walletsAndCommissions.getCommissionValue());
        } else if (orderType == OperationType.BUY) {
            orderCreateDto.setWalletIdCurrencyConvert(walletsAndCommissions.getSpendWalletId());
            orderCreateDto.setCurrencyConvertBalance(walletsAndCommissions.getSpendWalletActiveBalance());
            orderCreateDto.setComissionForBuyId(walletsAndCommissions.getCommissionId());
            orderCreateDto.setComissionForBuyRate(walletsAndCommissions.getCommissionValue());
        }
        /**/
        orderCreateDto.calculateAmounts();
        return orderCreateDto;
    }

    //+
    public OrderValidationDto validateOrder(OrderCreateDto orderCreateDto) {
        OrderValidationDto orderValidationDto = new OrderValidationDto();
        Map<String, Object> errors = orderValidationDto.getErrors();
        Map<String, Object[]> errorParams = orderValidationDto.getErrorParams();
        if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("amount_" + errors.size(), "order.fillfield");
        }
        if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("exrate_" + errors.size(), "order.fillfield");
        }
        final CurrencyPair currencyPair = orderCreateDto.getCurrencyPair();
        final OperationType operationType = orderCreateDto.getOperationType();
        final UserRole userRole = userService.getUserRoleFromSecurityContext();

        CurrencyPairLimitDto currencyPairLimit = currencyService.findLimitForRoleByCurrencyPairAndType(currencyPair, operationType, userRole);

        if (orderCreateDto.getOrderBaseType() != null && orderCreateDto.getOrderBaseType().equals(OrderBaseType.STOP_LIMIT)) {
            if (orderCreateDto.getStop() == null || orderCreateDto.getStop().compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("stop_" + errors.size(), "order.fillfield");
            } else {
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMinRate()) < 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.minrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMinRate()});
                }
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.maxrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMaxRate()});
                }
            }
        }
        /*------------------*/
        if (orderCreateDto.getCurrencyPair().getPairType() == CurrencyPairType.ICO) {
            validateIcoOrder(errors, orderCreateDto);
        }
        /*------------------*/
        if (orderCreateDto.getAmount() != null) {
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMaxAmount()) > 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.maxvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMinAmount()) < 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.minvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMinAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
        }
        if (orderCreateDto.getExchangeRate() != null) {
            if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) < 1) {
                errors.put("exrate_" + errors.size(), "order.zerorate");
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMinRate()) < 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.minrate");
                errorParams.put(key, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMinRate(), false)});
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.maxrate");
                errorParams.put(key, new Object[]{BigDecimalProcessingUtil.formatNonePoint(currencyPairLimit.getMaxRate(), false)});
            }

        }
        if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
            boolean ifEnoughMoney = orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0 && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
            if (!ifEnoughMoney) {
                errors.put("balance_" + errors.size(), "validation.orderNotEnoughMoney");
            }
        }
        return orderValidationDto;
    }

    //+
    private void validateIcoOrder(Map<String, Object> errors, OrderCreateDto orderCreateDto) {
        if (orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
            throw new RuntimeException("unsupported type of order");
        }
        if (orderCreateDto.getOperationType() == OperationType.SELL) {
            SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .filter(p -> p.getAuthority().equals(UserRole.ICO_MARKET_MAKER.name())).findAny().orElseThrow(() -> new RuntimeException("not allowed"));
        }
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            Optional<BigDecimal> lastRate = orderDao.getLowestOpenOrderPriceByCurrencyPairAndOperationType(orderCreateDto.getCurrencyPair().getId(), OperationType.SELL.type);
            if (!lastRate.isPresent() || orderCreateDto.getExchangeRate().compareTo(lastRate.get()) < 0) {
                errors.put("exrate_" + errors.size(), "order_ico.no_orders_for_rate");
            }
        }
    }

    //+
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
        ProfileData profileData = new ProfileData(200);
        try {
            String description = transactionDescription.get(null, action);
            int createdOrderId;
            int outWalletId;
            BigDecimal outAmount;
            if (orderCreateDto.getOperationType() == OperationType.BUY) {
                outWalletId = orderCreateDto.getWalletIdCurrencyConvert();
                outAmount = orderCreateDto.getTotalWithComission();
            } else {
                outWalletId = orderCreateDto.getWalletIdCurrencyBase();
                outAmount = orderCreateDto.getAmount();
            }
            if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
                profileData.setTime1();
                ExOrder exOrder = new ExOrder(orderCreateDto);
                OrderBaseType orderBaseType = orderCreateDto.getOrderBaseType();
                if (orderBaseType == null) {
                    CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                    orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                    exOrder.setOrderBaseType(orderBaseType);
                }
                TransactionSourceType sourceType;
                switch (orderBaseType) {
                    case STOP_LIMIT: {
                        createdOrderId = stopOrderService.createOrder(exOrder);
                        sourceType = TransactionSourceType.STOP_ORDER;
                        break;
                    }
                    case ICO: {
                        if (orderCreateDto.getOperationType() == OperationType.BUY) {
                            return 0;
                        }
                    }
                    default: {
                        createdOrderId = orderDao.createOrder(exOrder);
                        sourceType = TransactionSourceType.ORDER;
                    }
                }
                if (createdOrderId > 0) {
                    profileData.setTime2();
                    exOrder.setId(createdOrderId);
                    WalletTransferStatus result = walletService.walletInnerTransfer(
                            outWalletId,
                            outAmount.negate(),
                            sourceType,
                            exOrder.getId(),
                            description);
                    profileData.setTime3();
                    if (result != WalletTransferStatus.SUCCESS) {
                        throw new OrderCreationException(result.toString());
                    }
                    setStatus(createdOrderId, OrderStatus.OPENED, exOrder.getOrderBaseType());
                    profileData.setTime4();
                }
                return createdOrderId;

            } else {
                //this exception will be caught in controller, populated  with message text  and thrown further
                throw new NotEnoughUserWalletMoneyException("");
            }
        } finally {
            profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
        }
    }

    //+
    @Transactional
    public OrderCreateDto prepareOrderRest(OrderCreationParamsDto orderCreationParamsDto, String userEmail, Locale locale, OrderBaseType orderBaseType) {
        CurrencyPair activeCurrencyPair = currencyService.findCurrencyPairById(orderCreationParamsDto.getCurrencyPairId());
        OrderCreateDto orderCreateDto = prepareNewOrder(activeCurrencyPair, orderCreationParamsDto.getOrderType(),
                userEmail, orderCreationParamsDto.getAmount(), orderCreationParamsDto.getRate(), orderBaseType);
        log.debug("Order prepared" + orderCreateDto);
        OrderValidationDto orderValidationDto = validateOrder(orderCreateDto);
        Map<String, Object> errors = orderValidationDto.getErrors();
        if (!errors.isEmpty()) {
            errors.replaceAll((key, value) -> messageSource.getMessage(value.toString(), orderValidationDto.getErrorParams().get(key), locale));
            throw new OrderParamsWrongException(errors.toString());
        }
        return orderCreateDto;
    }

    //+
    @Transactional
    public OrderCreationResultDto createPreparedOrderRest(OrderCreateDto orderCreateDto, Locale locale) {
        Optional<OrderCreationResultDto> autoAcceptResult = autoAcceptOrders(orderCreateDto, locale);
        log.info("Auto accept result: " + autoAcceptResult);
        if (autoAcceptResult.isPresent()) {
            return autoAcceptResult.get();
        }
        OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

        Integer createdOrderId = createOrder(orderCreateDto, CREATE);
        if (createdOrderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        orderCreationResultDto.setCreatedOrderId(createdOrderId);
        log.info("Order creation result result: " + autoAcceptResult);
        return orderCreationResultDto;
    }

    //+
    @Transactional
    public OrderCreationResultDto prepareAndCreateOrderRest(String currencyPairName, OperationType orderType,
                                                            BigDecimal amount, BigDecimal exrate, String userEmail) {
        synchronized (restOrderCreationLock) {
            log.info(String.format("Start creating order: %s %s amount %s rate %s", currencyPairName, orderType.name(), amount, exrate));
            Locale locale = userService.getUserLocaleForMobile(userEmail);
            CurrencyPair currencyPair = currencyService.getCurrencyPairByName(currencyPairName);
            if (currencyPair.getPairType() != CurrencyPairType.MAIN) {
                throw new NotCreatableOrderException("This pair available only through website");
            }
            OrderCreateDto orderCreateDto = prepareOrderRest(new OrderCreationParamsDto(currencyPair.getId(), orderType, amount, exrate), userEmail, locale, OrderBaseType.LIMIT);
            return createPreparedOrderRest(orderCreateDto, locale);
        }
    }

    //+
    @Transactional(rollbackFor = Exception.class)
    public Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale) {
        synchronized (autoAcceptLock) {
            ProfileData profileData = new ProfileData(200);
            try {
                boolean acceptSameRoleOnly = userRoleService.isOrderAcceptionAllowedForUser(orderCreateDto.getUserId());
                List<ExOrder> acceptableOrders = orderDao.selectTopOrders(orderCreateDto.getCurrencyPair().getId(), orderCreateDto.getExchangeRate(),
                        OperationType.getOpposite(orderCreateDto.getOperationType()), acceptSameRoleOnly, userService.getUserRoleFromDB(orderCreateDto.getUserId()).getRole(), orderCreateDto.getOrderBaseType());
                profileData.setTime1();
                log.debug("acceptableOrders - " + OperationType.getOpposite(orderCreateDto.getOperationType()) + " : " + acceptableOrders);
                if (acceptableOrders.isEmpty()) {
                    return Optional.empty();
                }
                BigDecimal cumulativeSum = BigDecimal.ZERO;
                List<ExOrder> ordersForAccept = new ArrayList<>();
                ExOrder orderForPartialAccept = null;
                for (ExOrder order : acceptableOrders) {
                    cumulativeSum = cumulativeSum.add(order.getAmountBase());
                    if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
                        ordersForAccept.add(order);
                    } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) == 0) {
                        ordersForAccept.add(order);
                        break;
                    } else {
                        orderForPartialAccept = order;
                        break;
                    }
                }
                OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

                if (ordersForAccept.size() > 0) {
                    acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept.stream().map(ExOrder::getId).collect(toList()), locale);
                    orderCreationResultDto.setAutoAcceptedQuantity(ordersForAccept.size());
                }
                if (orderForPartialAccept != null) {
                    BigDecimal partialAcceptResult = acceptPartially(orderCreateDto, orderForPartialAccept, cumulativeSum, locale);
                    orderCreationResultDto.setPartiallyAcceptedAmount(partialAcceptResult);
                    orderCreationResultDto.setPartiallyAcceptedOrderFullAmount(orderForPartialAccept.getAmountBase());
                } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0 && orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
                    User user = userService.getUserById(orderCreateDto.getUserId());
                    profileData.setTime2();
                    OrderCreateDto remainderNew = prepareNewOrder(
                            orderCreateDto.getCurrencyPair(),
                            orderCreateDto.getOperationType(),
                            user.getEmail(),
                            orderCreateDto.getAmount().subtract(cumulativeSum),
                            orderCreateDto.getExchangeRate(),
                            orderCreateDto.getOrderBaseType());
                    profileData.setTime3();
                    Integer createdOrderId = createOrder(remainderNew, CREATE);
                    profileData.setTime4();
                    orderCreationResultDto.setCreatedOrderId(createdOrderId);
                }
                return Optional.of(orderCreationResultDto);
            } finally {
                profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
            }
        }
    }

    //+
    private BigDecimal acceptPartially(OrderCreateDto newOrder, ExOrder orderForPartialAccept, BigDecimal cumulativeSum, Locale locale) {
        deleteOrderForPartialAccept(orderForPartialAccept.getId());
        BigDecimal amountForPartialAccept = newOrder.getAmount().subtract(cumulativeSum.subtract(orderForPartialAccept.getAmountBase()));
        OrderCreateDto accepted = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
                userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), amountForPartialAccept,
                orderForPartialAccept.getExRate(), orderForPartialAccept.getId(), newOrder.getOrderBaseType());
        OrderCreateDto remainder = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
                userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), orderForPartialAccept.getAmountBase().subtract(amountForPartialAccept),
                orderForPartialAccept.getExRate(), orderForPartialAccept.getId(), newOrder.getOrderBaseType());
        int acceptedId = createOrder(accepted, CREATE);
        createOrder(remainder, OrderActionEnum.CREATE_SPLIT);
        acceptOrder(newOrder.getUserId(), acceptedId, locale);
        return amountForPartialAccept;
    }

    //+
    @Transactional(readOnly = true)
    public ExOrder getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    //+
    @Transactional
    public boolean setStatus(int orderId, OrderStatus status, OrderBaseType orderBaseType) {
        switch (orderBaseType) {
            case STOP_LIMIT: {
                return stopOrderService.setStatus(orderId, status);
            }
            default: {
                return this.setStatus(orderId, status);
            }
        }
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public boolean setStatus(int orderId, OrderStatus status) {
        return orderDao.setStatus(orderId, status);
    }

    //+
    @Transactional
    public void acceptOrder(String userEmail, Integer orderId) {
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        Integer userId = userService.getIdByEmail(userEmail);
        acceptOrdersList(userId, Collections.singletonList(orderId), locale);
    }

    //+
    @Transactional(rollbackFor = {Exception.class})
    public void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale) {
        if (orderDao.lockOrdersListForAcception(ordersList)) {
            for (Integer orderId : ordersList) {
                acceptOrder(userAcceptorId, orderId, locale);
            }
        } else {
            throw new OrderAcceptionException(messageSource.getMessage("order.lockerror", null, locale));
        }
    }

    //+
    @Transactional(rollbackFor = {Exception.class})
    public void acceptOrder(int userAcceptorId, int orderId, Locale locale) {
        try {
            ExOrder exOrder = this.getOrderById(orderId);

            checkAcceptPermissionForUser(userAcceptorId, exOrder.getUserId(), locale);

            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletService.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), userAcceptorId);
            String descriptionForCreator = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPTED);
            String descriptionForAcceptor = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPT);
            /**/
            if (walletsForOrderAcceptionDto.getOrderStatusId() != 2) {
                throw new AlreadyAcceptedOrderException(messageSource.getMessage("order.alreadyacceptederror", null, locale));
            }
            /**/
            int createdWalletId;
            if (exOrder.getOperationType() == OperationType.BUY) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            /**/
            /*calculate convert currency amount for creator - simply take stored amount from order*/
            BigDecimal amountWithComissionForCreator = getAmountWithComissionForCreator(exOrder);
            Commission comissionForCreator = new Commission();
            comissionForCreator.setId(exOrder.getComissionId());
            /*calculate convert currency amount for acceptor - calculate at the current commission rate*/
            OperationType operationTypeForAcceptor = exOrder.getOperationType() == OperationType.BUY ? OperationType.SELL : OperationType.BUY;
            Commission comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor, userService.getUserRoleFromDB(userAcceptorId));
            BigDecimal comissionRateForAcceptor = comissionForAcceptor.getValue();
            BigDecimal amountComissionForAcceptor = BigDecimalProcessingUtil.doAction(exOrder.getAmountConvert(), comissionRateForAcceptor, ActionType.MULTIPLY_PERCENT);
            BigDecimal amountWithComissionForAcceptor;
            if (exOrder.getOperationType() == OperationType.BUY) {
                amountWithComissionForAcceptor = BigDecimalProcessingUtil.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.SUBTRACT);
            } else {
                amountWithComissionForAcceptor = BigDecimalProcessingUtil.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.ADD);
            }
            /*determine the IN and OUT amounts for creator and acceptor*/
            BigDecimal creatorForOutAmount = null;
            BigDecimal creatorForInAmount = null;
            BigDecimal acceptorForOutAmount = null;
            BigDecimal acceptorForInAmount = null;
            BigDecimal commissionForCreatorOutWallet = null;
            BigDecimal commissionForCreatorInWallet = null;
            BigDecimal commissionForAcceptorOutWallet = null;
            BigDecimal commissionForAcceptorInWallet = null;
            if (exOrder.getOperationType() == OperationType.BUY) {
                commissionForCreatorOutWallet = exOrder.getCommissionFixedAmount();
                commissionForCreatorInWallet = BigDecimal.ZERO;
                commissionForAcceptorOutWallet = BigDecimal.ZERO;
                commissionForAcceptorInWallet = amountComissionForAcceptor;
                /**/
                creatorForOutAmount = amountWithComissionForCreator;
                creatorForInAmount = exOrder.getAmountBase();
                acceptorForOutAmount = exOrder.getAmountBase();
                acceptorForInAmount = amountWithComissionForAcceptor;
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                commissionForCreatorOutWallet = BigDecimal.ZERO;
                commissionForCreatorInWallet = exOrder.getCommissionFixedAmount();
                commissionForAcceptorOutWallet = amountComissionForAcceptor;
                commissionForAcceptorInWallet = BigDecimal.ZERO;
                /**/
                creatorForOutAmount = exOrder.getAmountBase();
                creatorForInAmount = amountWithComissionForCreator;
                acceptorForOutAmount = amountWithComissionForAcceptor;
                acceptorForInAmount = exOrder.getAmountBase();
            }
            WalletOperationData walletOperationData;
            WalletTransferStatus walletTransferStatus;
            String exceptionMessage = "";
            /**/
            /*for creator OUT*/
            walletOperationData = new WalletOperationData();
            walletService.walletInnerTransfer(
                    walletsForOrderAcceptionDto.getUserCreatorOutWalletId(),
                    creatorForOutAmount,
                    TransactionSourceType.ORDER,
                    exOrder.getId(),
                    descriptionForCreator);
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
            walletOperationData.setAmount(creatorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForCreator);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughreservedmoneyforcreator", locale);
                if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
                    throw new InsufficientCostsForAcceptionException(exceptionMessage);
                }
                throw new OrderAcceptionException(exceptionMessage);
            }
            /*for acceptor OUT*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId());
            walletOperationData.setAmount(acceptorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForAcceptor);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughmoneyforacceptor", locale);
                if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
                    throw new InsufficientCostsForAcceptionException(exceptionMessage);
                }
                throw new OrderAcceptionException(exceptionMessage);
            }
            /*for creator IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorInWalletId());
            walletOperationData.setAmount(creatorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForCreator);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
                throw new OrderAcceptionException(exceptionMessage);
            }

            /*for acceptor IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorInWalletId());
            walletOperationData.setAmount(acceptorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForAcceptor);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
                throw new OrderAcceptionException(exceptionMessage);
            }
            /**/
            CompanyWallet companyWallet = new CompanyWallet();
            companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
            companyWallet.setBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertBalance());
            companyWallet.setCommissionBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertCommissionBalance());
            companyWalletService.deposit(companyWallet, new BigDecimal(0), exOrder.getCommissionFixedAmount().add(amountComissionForAcceptor));
            /**/
            exOrder.setStatus(OrderStatus.CLOSED);
            exOrder.setDateAcception(LocalDateTime.now());
            exOrder.setUserAcceptorId(userAcceptorId);
            final Currency currency = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId())
                    .getCurrency2();

            referralService.processReferral(exOrder, exOrder.getCommissionFixedAmount(), currency, exOrder.getUserId()); //Processing referral for Order Creator
            referralService.processReferral(exOrder, amountComissionForAcceptor, currency, exOrder.getUserAcceptorId()); //Processing referral for Order Acceptor

            if (!updateOrder(exOrder)) {
                throw new OrderAcceptionException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
            }
        } catch (Exception e) {
            log.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
            throw e;
        }
    }

    //+
    private void checkAcceptPermissionForUser(Integer acceptorId, Integer creatorId, Locale locale) {
        UserRole acceptorRole = userService.getUserRoleFromDB(acceptorId);
        UserRole creatorRole = userService.getUserRoleFromDB(creatorId);

        UserRoleSettings creatorSettings = userRoleService.retrieveSettingsForRole(creatorRole.getRole());
        if (creatorSettings.isBotAcceptionAllowedOnly() && acceptorRole != UserRole.BOT_TRADER) {
            throw new AttemptToAcceptBotOrderException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
        }
        if (userRoleService.isOrderAcceptionAllowedForUser(acceptorId)) {
            if (acceptorRole != creatorRole) {
                throw new OrderAcceptionException(messageSource.getMessage("order.accept.wrongRole", new Object[]{creatorRole.name()}, locale));
            }
        }
    }

    //+
    private String getWalletTransferExceptionMessage(WalletTransferStatus status, String negativeBalanceMessageCode, Locale locale) {
        String message = "";
        switch (status) {
            case CAUSED_NEGATIVE_BALANCE:
                message = messageSource.getMessage(negativeBalanceMessageCode, null, locale);
                break;
            case CORRESPONDING_COMPANY_WALLET_NOT_FOUND:
                message = messageSource.getMessage("orders.companyWalletNotFound", null, locale);
                break;
            case WALLET_NOT_FOUND:
                message = messageSource.getMessage("orders.walletNotFound", null, locale);
                break;
            case WALLET_UPDATE_ERROR:
                message = messageSource.getMessage("orders.walletUpdateError", null, locale);
                break;
            case TRANSACTION_CREATION_ERROR:
                message = messageSource.getMessage("transaction.createerror", null, locale);
                break;
            default:
                message = messageSource.getMessage("orders.acceptsaveerror", null, locale);

        }
        return message;
    }

    //+
    private BigDecimal getAmountWithComissionForCreator(ExOrder exOrder) {
        if (exOrder.getOperationType() == OperationType.SELL) {
            return BigDecimalProcessingUtil.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.SUBTRACT);
        } else {
            return BigDecimalProcessingUtil.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.ADD);
        }
    }

    //+
    @Transactional
    public void cancelOrder(Integer orderId) {
        ExOrder exOrder = getOrderById(orderId);

        cancelOrder(exOrder);
    }

    @Transactional
    public void cancelOpenOrdersByCurrencyPair(String currencyPair) {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        List<ExOrder> openedOrders = orderDao.getOpenedOrdersByCurrencyPair(userId, currencyPair);

        openedOrders.forEach(this::cancelOrder);
    }

    @Transactional
    public void cancelAllOpenOrders() {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        List<ExOrder> openedOrders = orderDao.getAllOpenedOrdersByUserId(userId);

        openedOrders.forEach(this::cancelOrder);
    }

    private boolean cancelOrder(ExOrder exOrder) {
        return cancelOrder(exOrder, null);
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean cancelOrder(ExOrder exOrder, Locale locale) {
        if (isNull(locale)) {
            final String currentUserEmail = getUserEmailFromSecurityContext();

            final String creatorEmail = userService.getEmailById(exOrder.getUserId());
            if (!currentUserEmail.equals(creatorEmail)) {
                throw new IncorrectCurrentUserException(String.format("Creator email: %s and currentUser email: %s are different", creatorEmail, currentUserEmail));
            }

            locale = userService.getUserLocaleForMobile(currentUserEmail);
        }
        try {
            WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                    exOrder.getId(),
                    exOrder.getOperationType());
            OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
            if (currentStatus != OrderStatus.OPENED) {
                throw new OrderAcceptionException(messageSource.getMessage("order.cannotcancel", null, locale));
            }
            String description = transactionDescription.get(currentStatus, CANCEL);
            WalletTransferStatus transferResult = walletService.walletInnerTransfer(
                    walletsForOrderCancelDto.getWalletId(),
                    walletsForOrderCancelDto.getReservedAmount(),
                    TransactionSourceType.ORDER,
                    exOrder.getId(),
                    description);
            if (transferResult != WalletTransferStatus.SUCCESS) {
                throw new OrderCancellingException(transferResult.toString());
            }
            return setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        } catch (Exception e) {
            log.error("Error while cancelling order " + exOrder.getId() + " , " + e.getLocalizedMessage());
            throw e;
        }
    }

    private String getUserEmailFromSecurityContext() {
        return userService.getUserEmailFromSecurityContext();
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public boolean updateOrder(ExOrder exOrder) {
        return orderDao.updateOrder(exOrder);
    }

    //+
    @Transactional(rollbackFor = {Exception.class})
    public Integer deleteOrderForPartialAccept(int orderId) {
        Object result = deleteOrder(orderId, OrderStatus.SPLIT_CLOSED, DELETE_SPLIT);
        if (result instanceof OrderDeleteStatus) {
            throw new OrderDeletingException(result.toString());
        }
        return (Integer) result;
    }

    //+
    @Transactional(readOnly = true)
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType) {
        UserRole userRole;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            userRole = userService.getUserRoleFromDB(email);
        } else {
            userRole = userService.getUserRoleFromSecurityContext();
        }
        return orderDao.getWalletAndCommission(email, currency, operationType, userRole);
    }

    //+
    @Transactional
    Object deleteOrder(int orderId, OrderStatus newOrderStatus, OrderActionEnum action) {
        List<OrderDetailDto> list = walletService.getOrderRelatedDataAndBlock(orderId);
        if (list.isEmpty()) {
            return OrderDeleteStatus.NOT_FOUND;
        }
        int processedRows = 1;
        /**/
        OrderStatus currentOrderStatus = list.get(0).getOrderStatus();
        String description = transactionDescription.get(currentOrderStatus, action);
        /**/
        if (!setStatus(orderId, newOrderStatus)) {
            return OrderDeleteStatus.ORDER_UPDATE_ERROR;
        }
        /**/
        for (OrderDetailDto orderDetailDto : list) {
            if (currentOrderStatus == OrderStatus.CLOSED) {
                if (orderDetailDto.getCompanyCommission().compareTo(BigDecimal.ZERO) != 0) {
                    Integer companyWalletId = orderDetailDto.getCompanyWalletId();
                    if (companyWalletId != 0 && !companyWalletService.substractCommissionBalanceById(companyWalletId, orderDetailDto.getCompanyCommission())) {
                        return OrderDeleteStatus.COMPANY_WALLET_UPDATE_ERROR;
                    }
                }
                /**/
                WalletOperationData walletOperationData = new WalletOperationData();
                OperationType operationType = null;
                if (orderDetailDto.getTransactionType() == OperationType.OUTPUT) {
                    operationType = OperationType.INPUT;
                } else if (orderDetailDto.getTransactionType() == OperationType.INPUT) {
                    operationType = OperationType.OUTPUT;
                }
                if (operationType != null) {
                    walletOperationData.setOperationType(operationType);
                    walletOperationData.setWalletId(orderDetailDto.getUserWalletId());
                    walletOperationData.setAmount(orderDetailDto.getTransactionAmount());
                    walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                    Commission commission = commissionDao.getDefaultCommission(OperationType.STORNO);
                    walletOperationData.setCommission(commission);
                    walletOperationData.setCommissionAmount(commission.getValue());
                    walletOperationData.setSourceType(TransactionSourceType.ORDER);
                    walletOperationData.setSourceId(orderId);
                    walletOperationData.setDescription(description);
                    WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
                    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                        return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                    }
                }
                log.debug("rows before refs {}", processedRows);
                int processedRefRows = this.unprocessReferralTransactionByOrder(orderDetailDto.getOrderId(), description);
                processedRows = processedRefRows + processedRows;
                log.debug("rows after refs {}", processedRows);
                /**/
                if (!transactionService.setStatusById(
                        orderDetailDto.getTransactionId(),
                        TransactionStatus.DELETED.getStatus())) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
                /**/
                processedRows++;
            } else if (currentOrderStatus == OrderStatus.OPENED) {
                WalletTransferStatus walletTransferStatus = walletService.walletInnerTransfer(
                        orderDetailDto.getOrderCreatorReservedWalletId(),
                        orderDetailDto.getOrderCreatorReservedAmount(),
                        TransactionSourceType.ORDER,
                        orderId,
                        description);
                if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                    return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                }
                /**/
                if (!transactionService.setStatusById(
                        orderDetailDto.getTransactionId(),
                        TransactionStatus.DELETED.getStatus())) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
            }
        }
        return processedRows;
    }

    //+
    private int unprocessReferralTransactionByOrder(int orderId, String description) {
        List<Transaction> transactions = transactionService.getPayedRefTransactionsByOrderId(orderId);
        for (Transaction transaction : transactions) {
            WalletTransferStatus walletTransferStatus = null;
            try {
                WalletOperationData walletOperationData = new WalletOperationData();
                walletOperationData.setWalletId(transaction.getUserWallet().getId());
                walletOperationData.setAmount(transaction.getAmount());
                walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                walletOperationData.setCommission(transaction.getCommission());
                walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
                walletOperationData.setSourceType(TransactionSourceType.REFERRAL);
                walletOperationData.setSourceId(transaction.getSourceId());
                walletOperationData.setDescription(description);
                walletOperationData.setOperationType(OperationType.OUTPUT);
                walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
                referralService.setRefTransactionStatus(ReferralTransactionStatusEnum.DELETED, transaction.getSourceId());
                companyWalletService.substractCommissionBalanceById(transaction.getCompanyWallet().getId(), transaction.getAmount().negate());
            } catch (Exception e) {
                log.error("error unprocess ref transactions" + e);
            }
            log.debug("status " + walletTransferStatus);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new RuntimeException("can't unprocess referral transaction for order " + orderId);
            }
        }
        log.debug("end unprocess refs ");
        return transactions.size();
    }

    //+
//    public List<OpenOrderDto> getOpenOrders(String currencyPairName, OrderType orderType) {
//        Integer currencyPairId = currencyService.findCurrencyPairByName(currencyPairName);
//        return orderDao.getOpenOrders(currencyPairId, orderType);
//    }

    //+
    @Transactional(readOnly = true)
    public List<CoinmarketApiDto> getDailyCoinmarketData(String pairName) {
        if (nonNull(pairName)) {
            validateCurrencyPair(pairName);
        }
        return isNull(pairName) && isNotEmpty(coinmarketCachedData)
                ? coinmarketCachedData
                : getCoinmarketDataForActivePairs(pairName);
    }

    //+
    @Transactional(readOnly = true)
    public Map<OrderType, List<OrderBookItem>> getOrderBook(String pairName,
                                                            @Null OrderType orderType,
                                                            @Null Integer limit) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairByName(pairName);

        return nonNull(orderType)
                ? Collections.singletonMap(orderType, orderDao.getOrderBookItemsByType(currencyPair, orderType, limit))
                : orderDao.getOrderBookItems(currencyPair, limit).stream()
                .sorted(Comparator.comparing(OrderBookItem::getOrderType).thenComparing(OrderBookItem::getRate))
                .collect(groupingBy(OrderBookItem::getOrderType));
    }

    //+
    @Transactional(readOnly = true)
    public List<TradeHistoryDto> getTradeHistory(String pairName,
                                                 @NotNull LocalDate fromDate,
                                                 @NotNull LocalDate toDate,
                                                 @Null Integer limit) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairByName(pairName);

        return orderDao.getTradeHistory(
                currencyPair,
                LocalDateTime.of(fromDate, LocalTime.MIN),
                LocalDateTime.of(toDate, LocalTime.MAX),
                limit);
    }

    //+
    @Transactional(readOnly = true)
    public List<CandleChartItemDto> getDataForCandleChart(String pairName, BackDealInterval interval) {
        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(pairName);

        return orderDao.getDataForCandleChart(currencyPair, interval);
    }

    //+
    @Transactional(readOnly = true)
    public List<UserOrdersDto> getUserOpenOrders(@Null String pairName,
                                                 @NotNull Integer limit) {
        final int userId = userService.getAuthenticatedUserId();

        CurrencyPair currencyPair = isNull(pairName) ? null : currencyService.findCurrencyPairByName(pairName);

        return orderDao.getUserOrdersByStatus(userId, currencyPair, OrderStatus.OPENED, limit);
    }

    @Transactional(readOnly = true)
    public List<UserOrdersDto> getUserClosedOrders(@Null String pairName,
                                                   @NotNull Integer limit) {
        final int userId = userService.getAuthenticatedUserId();

        CurrencyPair currencyPair = isNull(pairName) ? null : currencyService.findCurrencyPairByName(pairName);

        return orderDao.getUserOrdersByStatus(userId, currencyPair, OrderStatus.CLOSED, limit);
    }

    @Transactional(readOnly = true)
    public List<UserOrdersDto> getUserCanceledOrders(@Null String pairName,
                                                     @NotNull Integer limit) {
        final int userId = userService.getAuthenticatedUserId();

        CurrencyPair currencyPair = isNull(pairName) ? null : currencyService.findCurrencyPairByName(pairName);

        return orderDao.getUserOrdersByStatus(userId, currencyPair, OrderStatus.CANCELLED, limit);
    }

    //+
    @Transactional(readOnly = true)
    public CommissionDto getAllCommissions() {
        UserRole userRole = userService.getUserRoleFromSecurityContext();

        return orderDao.getAllCommissions(userRole);
    }

    //+
    @Transactional(readOnly = true)
    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(String currencyPairName,
                                                                       @NotNull LocalDate fromDate,
                                                                       @NotNull LocalDate toDate,
                                                                       @NotNull Integer limit) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairByName(currencyPairName);
        final int userId = userService.getAuthenticatedUserId();

        return orderDao.getUserTradeHistoryByCurrencyPair(
                userId,
                currencyPair,
                LocalDateTime.of(fromDate, LocalTime.MIN),
                LocalDateTime.of(toDate, LocalTime.MAX),
                limit);
    }

    //+
    @Transactional(readOnly = true)
    public List<TransactionDto> getOrderTransactions(Integer orderId) {
        final int userId = userService.getAuthenticatedUserId();

        return orderDao.getOrderTransactions(userId, orderId);
    }

    private void validateCurrencyPair(String pairName) {
        currencyService.findCurrencyPairByName(pairName);
    }

    private List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String pairName) {
        return orderDao.getCoinmarketData(pairName);
    }
}


