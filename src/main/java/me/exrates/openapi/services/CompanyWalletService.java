package me.exrates.openapi.services;

import me.exrates.openapi.exceptions.NotEnoughUserWalletMoneyException;
import me.exrates.openapi.exceptions.WalletPersistException;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.repositories.CompanyWalletDao;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static me.exrates.openapi.models.enums.ActionType.SUBTRACT;

@Service
public class CompanyWalletService {

    private final CompanyWalletDao companyWalletDao;

    @Autowired
    public CompanyWalletService(CompanyWalletDao companyWalletDao) {
        this.companyWalletDao = companyWalletDao;
    }

    //+
    @Transactional(readOnly = true)
    public CompanyWallet findByCurrency(Currency currency) {
        return companyWalletDao.findByCurrencyId(currency);
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public void deposit(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().add(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed deposit on company wallet " + companyWallet.toString());
        }
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount) {
        BigDecimal newReservedBalance = BigDecimalProcessingUtil.doAction(companyWallet.getCommissionBalance(), amount, SUBTRACT);
        if (newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setCommissionBalance(newReservedBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }

    //+
    @Transactional
    public boolean substractCommissionBalanceById(Integer id, BigDecimal amount) {
        return companyWalletDao.substarctCommissionBalanceById(id, amount);
    }
}
