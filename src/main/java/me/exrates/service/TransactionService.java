package me.exrates.service;

import me.exrates.model.Transaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

}
