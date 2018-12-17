package me.exrates.openapi.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.CompanyWalletDao;
import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.service.CompanyWalletService;
import me.exrates.openapi.service.CurrencyService;
import me.exrates.openapi.exception.service.NotEnoughUserWalletMoneyException;
import me.exrates.openapi.exception.service.WalletPersistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.exrates.openapi.model.enums.ActionType.SUBTRACT;
import static me.exrates.openapi.model.util.BigDecimalProcessing.doAction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2
@Service
public class CompanyWalletServiceImpl implements CompanyWalletService {

    private static final Logger logger = LogManager.getLogger(CompanyWalletServiceImpl.class);
    @Autowired
    private CompanyWalletDao companyWalletDao;
    @Autowired
    private CurrencyService currencyService;

    @Override
    public CompanyWallet create(Currency currency) {
        return companyWalletDao.create(currency);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyWallet> getCompanyWallets() {
        List<CompanyWallet> compWalletList = new ArrayList<CompanyWallet>();
        List<Currency> currList = currencyService.getAllActiveCurrencies();
        for (Currency c : currList) {
            CompanyWallet cw = this.findByCurrency(c);
            if (cw != null) {
                compWalletList.add(cw);
            }
        }
        return compWalletList;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyWallet findByCurrency(Currency currency) {
        return companyWalletDao.findByCurrencyId(currency);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyWallet findByWalletId(int walletId) {
        CompanyWallet result = companyWalletDao.findByWalletId(walletId);
        result.setCurrency(currencyService.findById(result.getCurrency().getId()));
        return result;
    }

    @Override
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

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdraw(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().subtract(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }


    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount) {
        BigDecimal newReservedBalance = doAction(companyWallet.getCommissionBalance(), amount, SUBTRACT);
        if (newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setCommissionBalance(newReservedBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyWallet> getCompanyWalletsSummaryForPermittedCurrencyList(Integer requesterUserId) {
        Set<String> permittedCurrencies = currencyService.getCurrencyPermittedNameList(requesterUserId);
        return  getCompanyWallets().stream()
                .filter(e->permittedCurrencies.contains(e.getCurrency().getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean substractCommissionBalanceById(Integer id, BigDecimal amount){
        return companyWalletDao.substarctCommissionBalanceById(id, amount);
    }
}
