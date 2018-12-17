package me.exrates.openapi.dao;

import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.Currency;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyWalletDao {

    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrencyId(Currency currency);

    boolean update(CompanyWallet companyWallet);

    CompanyWallet findByWalletId(int walletId);

    boolean substarctCommissionBalanceById(Integer id, BigDecimal amount);
}