package me.exrates.openapi.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.ReferralLevelDao;
import me.exrates.openapi.dao.ReferralTransactionDao;
import me.exrates.openapi.dao.ReferralUserGraphDao;
import me.exrates.openapi.model.Commission;
import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.ReferralLevel;
import me.exrates.openapi.model.ReferralTransaction;
import me.exrates.openapi.model.Wallet;
import me.exrates.openapi.model.enums.ActionType;
import me.exrates.openapi.model.enums.NotificationEvent;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.ReferralTransactionStatusEnum;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.vo.WalletOperationData;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static me.exrates.openapi.model.vo.WalletOperationData.BalanceType.ACTIVE;
import static me.exrates.openapi.utils.BigDecimalProcessingUtil.doAction;

@Log4j2
@Service
public class ReferralService {

    @Autowired
    private ReferralLevelDao referralLevelDao;
    @Autowired
    private ReferralUserGraphDao referralUserGraphDao;
    @Autowired
    private ReferralTransactionDao referralTransactionDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommissionService commissionService;

    private Commission commission;

    /**
     * URL following format  - xxx/register?ref=
     * where xxx is replaced by the domain name depending on the maven profile
     */
    private
    @Value("${referral.url}")
    String referralUrl;

    @PostConstruct
    public void init() {
        this.commission = commissionService.getDefaultCommission(OperationType.REFERRAL);
    }

    //+
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(final ExOrder exOrder, final BigDecimal commissionAmount, Currency currency, int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);
        Integer parent = null;
        for (ReferralLevel level : levels) {
            if (parent == null) {
                parent = referralUserGraphDao.getParent(userId);
            } else {
                parent = referralUserGraphDao.getParent(parent);
            }
            if (parent != null && !level.getPercent().equals(ZERO)) {
                final ReferralTransaction referralTransaction = new ReferralTransaction();
                referralTransaction.setExOrder(exOrder);
                referralTransaction.setReferralLevel(level);
                referralTransaction.setUserId(parent);
                referralTransaction.setInitiatorId(userId);
                int walletId = walletService.getWalletId(parent, currency.getId()); // Mutable variable
                if (walletId == 0) { // Wallet is absent, creating new wallet
                    final Wallet wallet = new Wallet();
                    wallet.setActiveBalance(ZERO);
                    wallet.setCurrencyId(currency.getId());
                    wallet.setUser(userService.getUserById(parent));
                    wallet.setReservedBalance(ZERO);
                    walletId = walletService.createNewWallet(wallet); // Changing mutable variable state
                }
                final ReferralTransaction createdRefTransaction = referralTransactionDao.create(referralTransaction);
                final BigDecimal amount = doAction(commissionAmount, level.getPercent(), ActionType.MULTIPLY_PERCENT);
                final WalletOperationData wod = new WalletOperationData();
                wod.setCommissionAmount(this.commission.getValue());
                wod.setCommission(this.commission);
                wod.setAmount(amount);
                wod.setWalletId(walletId);
                wod.setBalanceType(ACTIVE);
                wod.setOperationType(OperationType.INPUT);
                wod.setSourceType(TransactionSourceType.REFERRAL);
                wod.setSourceId(createdRefTransaction.getId());
                walletService.walletBalanceChange(wod);
                companyWalletService.withdrawReservedBalance(cWallet, amount);
                notificationService.createLocalizedNotification(parent, NotificationEvent.IN_OUT,
                        "referral.title", "referral.message",
                        new Object[]{BigDecimalProcessingUtil.formatNonePoint(amount, false), currency.getName()});
            } else {
                break;
            }
        }
    }

    //+
    @Transactional
    public void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId) {
        referralTransactionDao.setRefTransactionStatus(status, refTransactionId);
    }
}
