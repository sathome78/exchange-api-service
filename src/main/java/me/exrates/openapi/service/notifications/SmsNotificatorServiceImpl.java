package me.exrates.openapi.service.notifications;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.SmsSubscriptionDao;
import me.exrates.openapi.exceptions.IncorrectSmsPinException;
import me.exrates.openapi.exceptions.MessageUndeliweredException;
import me.exrates.openapi.exceptions.PaymentException;
import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.enums.NotificationPayEventEnum;
import me.exrates.openapi.model.dto.SmsSubscriptionDto;
import me.exrates.openapi.model.enums.ActionType;
import me.exrates.openapi.model.enums.NotificationTypeEnum;
import me.exrates.openapi.model.enums.NotificatorSubscriptionStateEnum;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.WalletTransferStatus;
import me.exrates.openapi.model.vo.WalletOperationData;
import me.exrates.openapi.service.CompanyWalletService;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.service.UserService;
import me.exrates.openapi.service.WalletService;
import me.exrates.openapi.service.notifications.sms.epochta.EpochtaApi;
import me.exrates.openapi.service.notifications.sms.epochta.Phones;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static me.exrates.openapi.model.vo.WalletOperationData.BalanceType.ACTIVE;

@Log4j2(topic = "message_notify")
@Component
public class SmsNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private SmsSubscriptionDao subscriptionDao;
    @Autowired
    private EpochtaApi smsService;
    @Autowired
    private CompanyWalletService companyWalletService;

    private static final String CURRENCY_NAME = "USD";
    private static final String SENDER = "Exrates";

    @Transactional
    public String sendRegistrationMessageToUser(String userEmail, String message) {
        int userId = userService.getIdByEmail(userEmail);
        int roleId = userService.getUserRoleFromDB(userId).getRole();
        BigDecimal messagePrice = notificatorsService.getMessagePrice(getNotificationType().getCode(), roleId);
        SmsSubscriptionDto subscriptionDto = subscriptionDao.getByUserId(userService.getIdByEmail(userEmail));
        pay(
                messagePrice,
                subscriptionDto.getNewPrice(),
                userId,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.BUY_ONE.name())
        );
        send(subscriptionDto.getNewContact(), message);
        return String.valueOf(subscriptionDto.getContact());
    }

    @Transactional
    public String send(String contact, String message) {
        log.debug("send sms to {}, message {}", contact, message);
        String xml = smsService.sendSms(SENDER, message,
                new ArrayList<Phones>() {{
                    add(new Phones("id1", "", contact));
                }});
        log.debug("send sms status {}", xml);
        String status;
        try {
            status = smsService.getValueFromXml(xml, "status");
            if (Integer.parseInt(status) < 1) {
                throw new MessageUndeliweredException();
            }
        } catch (Exception e) {
            throw new MessageUndeliweredException();
        }
        return xml;
    }

    @Transactional
    @Override
    public Object subscribe(Object subscriptionObject) {
        SmsSubscriptionDto recievedDto = (SmsSubscriptionDto) subscriptionObject;
        SmsSubscriptionDto userDto = Preconditions.checkNotNull(getByUserId(recievedDto.getUserId()));
        if (recievedDto.getCode().equals(userDto.getCode())) {
            userDto.setStateEnum(NotificatorSubscriptionStateEnum.getFinalState());
            userDto.setCode(null);
            userDto.setPriceForContact(userDto.getNewPrice());
            userDto.setContact(userDto.getNewContact());
            userDto.setNewPrice(null);
            userDto.setNewContact(null);
            createOrUpdate(userDto);
            return userDto;
        }
        throw new IncorrectSmsPinException();
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.SMS;
    }

    @Transactional
    public BigDecimal pay(BigDecimal feePercent, BigDecimal deliveryAmount, int userId, String description) {
        BigDecimal feeAmount = BigDecimalProcessingUtil.doAction(deliveryAmount, feePercent, ActionType.MULTIPLY_PERCENT);
        BigDecimal totalAmount = BigDecimalProcessingUtil.doAction(feeAmount, deliveryAmount, ActionType.ADD);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(OperationType.OUTPUT);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currencyService.findByName(CURRENCY_NAME).getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setCommissionAmount(feeAmount);
        walletOperationData.setAmount(totalAmount);
        walletOperationData.setSourceType(TransactionSourceType.NOTIFICATIONS);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyService.findByName(CURRENCY_NAME));
        companyWalletService.deposit(companyWallet, new BigDecimal(0), feeAmount);
        return totalAmount;
    }

    private void createOrUpdate(SmsSubscriptionDto dto) {
        if (getByUserId(dto.getUserId()) == null) {
            subscriptionDao.create(dto);
        } else {
            subscriptionDao.update(dto);
        }
    }

    public SmsSubscriptionDto getByUserId(int userId) {
        return subscriptionDao.getByUserId(userId);
    }
}
