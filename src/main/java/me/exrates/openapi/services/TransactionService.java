package me.exrates.openapi.services;

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

    @Transactional(readOnly = true)
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return transactionRepository.getPayedRefTransactionsByOrderId(orderId);
    }

    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return !transactionRepository.setStatusById(trasactionId, statusId);
    }
}
