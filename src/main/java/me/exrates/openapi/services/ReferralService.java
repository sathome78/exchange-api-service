package me.exrates.openapi.services;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.models.*;
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
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(ExOrder order,
                                BigDecimal commissionAmount,
                                Currency currency,
                                int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        Integer parent = null;
        for (ReferralLevel level : levels) {
            parent = isNull(parent) ? referralUserGraphDao.getParent(userId) : referralUserGraphDao.getParent(parent);

            if (nonNull(parent) && !(level.getPercent().compareTo(BigDecimal.ZERO) == 0)) {
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

                WalletOperationData wod = WalletOperationData.builder()
                        .commissionAmount(commission.getValue())
                        .commission(commission)
                        .amount(amount)
                        .walletId(walletId)
                        .balanceType(ACTIVE)
                        .operationType(OperationType.INPUT)
                        .sourceType(TransactionSourceType.REFERRAL)
                        .sourceId(createdRefTransaction.getId())
                        .build();

                walletService.walletBalanceChange(wod);
                companyWalletService.withdrawReservedBalance(companyWallet, amount);
            } else {
                break;
            }
        }
    }

    //+
    @Transactional
    public void setRefTransactionStatus(int refTransactionId) {
        referralTransactionDao.setRefTransactionStatus(refTransactionId);
    }
}
