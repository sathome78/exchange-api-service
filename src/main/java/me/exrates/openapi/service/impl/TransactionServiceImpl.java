package me.exrates.openapi.service.impl;

import me.exrates.openapi.dao.TransactionDao;
import me.exrates.openapi.model.Merchant;
import me.exrates.openapi.model.Transaction;
import me.exrates.openapi.model.dto.OperationViewDto;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.TransactionType;
import me.exrates.openapi.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);


    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

    private void updateAmount(Transaction transaction, BigDecimal amount) {
        int scale = currencyService.resolvePrecision(transaction.getCurrency().getName());
        BigDecimal commissionRate = transaction.getCommission().getValue();
        BigDecimal commission = calculateCommissionFromAmpunt(amount, commissionRate, scale);
        final BigDecimal newAmount = amount.subtract(commission).setScale(scale, ROUND_HALF_UP);
        transaction.setCommissionAmount(commission);
        transaction.setAmount(newAmount);
        transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
    }

    private BigDecimal calculateCommissionFromAmpunt(BigDecimal amount, BigDecimal commissionRate, int scale) {
        BigDecimal mass = BigDecimal.valueOf(100L).add(commissionRate);
        return amount.multiply(commissionRate)
                .divide(mass, scale, ROUND_HALF_UP).setScale(scale, ROUND_HALF_UP);
    }

    private void setTransactionMerchantAndOrder(OperationViewDto view, Transaction transaction) {
        TransactionSourceType sourceType = transaction.getSourceType();
        OperationType operationType = transaction.getOperationType();
        BigDecimal amount = transaction.getAmount();
        view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(sourceType, operationType, amount));
        if (sourceType == TransactionSourceType.REFILL || sourceType == TransactionSourceType.WITHDRAW) {
            view.setMerchant(transaction.getMerchant());
        } else {
            view.setMerchant(new Merchant(0, sourceType.name(), sourceType.name()));
        }

    }

    @Override
    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return transactionDao.setStatusById(trasactionId, statusId);
    }


    @Override
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return transactionDao.getPayedRefTransactionsByOrderId(orderId);
    }

}
