package me.exrates.service;

import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyWalletService {

    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrency(Currency currency);

    List<CompanyWallet> getCompanyWallets();

    void withdraw(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount);

    void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount);

    void deposit(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount);

    CompanyWallet findByWalletId(int walletId);

    List<CompanyWallet> getCompanyWalletsSummaryForPermittedCurrencyList(Integer requesterUserId);

    boolean substractCommissionBalanceById(Integer id, BigDecimal amount);
}