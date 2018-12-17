package me.exrates.openapi.service;

import me.exrates.openapi.model.Transaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

}
