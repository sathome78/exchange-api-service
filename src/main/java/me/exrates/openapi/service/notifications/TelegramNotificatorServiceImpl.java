package me.exrates.openapi.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.TelegramSubscriptionDao;
import me.exrates.openapi.exceptions.PaymentException;
import me.exrates.openapi.exceptions.TelegramSubscriptionException;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.dto.TelegramSubscription;
import me.exrates.openapi.model.enums.NotificationTypeEnum;
import me.exrates.openapi.model.enums.NotificatorSubscriptionStateEnum;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.UserRole;
import me.exrates.openapi.model.enums.WalletTransferStatus;
import me.exrates.openapi.model.vo.WalletOperationData;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.service.UserService;
import me.exrates.openapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Optional;

import static me.exrates.openapi.model.vo.WalletOperationData.BalanceType.ACTIVE;

@Log4j2(topic = "message_notify")
@Component("telegramNotificatorServiceImpl")
public class TelegramNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private TelegramSubscriptionDao subscribtionDao;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private NotificatorsService notificatorsService;

    private Currency currency;
    private static final String CURRENCY_NAME_FOR_PAY = "USD";

    @PostConstruct
    private void init() {
        currency = currencyService.findByName(CURRENCY_NAME_FOR_PAY);
    }

    @Transactional
    @Override
    public Object subscribe(Object subscribeData) {
        TelegramSubscription subscriptionDto = (TelegramSubscription) subscribeData;
        String[] data = (subscriptionDto.getRawText()).split(":");
        String email = data[0];
        Optional<TelegramSubscription> subscriptionOptional = subscribtionDao.getSubscribtionByCodeAndEmail(subscriptionDto.getRawText(), email);
        TelegramSubscription subscription = subscriptionOptional.orElseThrow(TelegramSubscriptionException::new);
        NotificatorSubscriptionStateEnum nextState = subscription.getSubscriptionState().getNextState();
        if (subscription.getSubscriptionState().isFinalState()) {
            /*set New account for subscription if allready subscribed*/
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        } else if (subscription.getSubscriptionState().isBeginState()) {
            subscription.setSubscriptionState(nextState);
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        }
        subscribtionDao.updateSubscription(subscription);
        return null;
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.TELEGRAM;
    }

    @Transactional
    public BigDecimal payForSubscribe(String userEmail, OperationType operationType,
                                      String description) {
        int userId = userService.getIdByEmail(userEmail);
        UserRole role = userService.getUserRoleFromDB(userEmail);
        BigDecimal fee = notificatorsService.getSubscriptionPrice(getNotificationType().getCode(), role.getRole());
        if (fee.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setCommissionAmount(fee);
        walletOperationData.setOperationType(operationType);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currency.getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setAmount(fee);
        walletOperationData.setSourceType(TransactionSourceType.NOTIFICATIONS);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        return fee;
    }
}
