package me.exrates.openapi.services;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.ReferralLevel;
import me.exrates.openapi.models.ReferralTransaction;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.enums.ActionType;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.ReferralTransactionStatus;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.ReferralLevelDao;
import me.exrates.openapi.repositories.ReferralTransactionDao;
import me.exrates.openapi.repositories.ReferralUserGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.openapi.models.vo.WalletOperationData.BalanceType.ACTIVE;
import static me.exrates.openapi.utils.BigDecimalProcessingUtil.doAction;

@Log4j2
@Service
public class ReferralService {

    private final ReferralLevelDao referralLevelDao;
    private final ReferralUserGraphDao referralUserGraphDao;
    private final ReferralTransactionDao referralTransactionDao;
    private final WalletService walletService;
    private final UserService userService;
    private final CompanyWalletService companyWalletService;
    private final CommissionService commissionService;

    private Commission commission;

    @Autowired
    public ReferralService(ReferralLevelDao referralLevelDao,
                           ReferralUserGraphDao referralUserGraphDao,
                           ReferralTransactionDao referralTransactionDao,
                           WalletService walletService,
                           UserService userService,
                           CompanyWalletService companyWalletService,
                           CommissionService commissionService) {
        this.referralLevelDao = referralLevelDao;
        this.referralUserGraphDao = referralUserGraphDao;
        this.referralTransactionDao = referralTransactionDao;
        this.walletService = walletService;
        this.userService = userService;
        this.companyWalletService = companyWalletService;
        this.commissionService = commissionService;
    }

    @PostConstruct
    public void init() {
        this.commission = commissionService.getDefaultCommission(OperationType.REFERRAL);
    }

    //+
    @Transactional
    public void setRefTransactionStatus(ReferralTransactionStatus status, int refTransactionId) {
        referralTransactionDao.setRefTransactionStatus(status, refTransactionId);
    }

    //+
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(final ExOrder order,
                                final BigDecimal commissionAmount,
                                Currency currency,
                                int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        Integer parent = null;
        for (ReferralLevel level : levels) {
            parent = isNull(parent) ? referralUserGraphDao.getParent(userId) : referralUserGraphDao.getParent(parent);

            if (nonNull(parent) && !level.getPercent().equals(BigDecimal.ZERO)) {
                ReferralTransaction referralTransaction = ReferralTransaction.builder()
                        .order(order)
                        .referralLevel(level)
                        .userId(parent)
                        .initiatorId(userId)
                        .build();

                int walletId = walletService.getWalletId(parent, currency.getId()); // Mutable variable

                if (walletId == 0) { // Wallet is absent, creating new wallet
                    User user = userService.getUserById(parent);

                    Wallet wallet = Wallet.builder()
                            .currencyId(currency.getId())
                            .user(user)
                            .activeBalance(BigDecimal.ZERO)
                            .reservedBalance(BigDecimal.ZERO)
                            .build();
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
                companyWalletService.withdrawReservedBalance(companyWallet, amount);
            } else {
                break;
            }
        }
    }
}
