package me.exrates.openapi.service;

import me.exrates.openapi.dao.TransactionDao;
import me.exrates.openapi.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return transactionDao.setStatusById(trasactionId, statusId);
    }

    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return transactionDao.getPayedRefTransactionsByOrderId(orderId);
    }
}
