package me.exrates.openapi.services;

import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Loggable(caption = "Get payed referral transactions by order id")
    @Transactional(readOnly = true)
    public List<Transaction> getPayedReferralTransactionsByOrderId(int orderId) {
        return transactionRepository.getPayedRefTransactionsByOrderId(orderId);
    }

    @Loggable(caption = "Set transaction status by id")
    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return !transactionRepository.setStatusById(trasactionId, statusId);
    }
}
