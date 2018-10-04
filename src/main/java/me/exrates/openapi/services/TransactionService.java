package me.exrates.openapi.services;

import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.repositories.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return transactionDao.getPayedRefTransactionsByOrderId(orderId);
    }

    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return !transactionDao.setStatusById(trasactionId, statusId);
    }
}
