package me.exrates.openapi.services;

import me.exrates.openapi.exceptions.NotEnoughUserWalletMoneyException;
import me.exrates.openapi.exceptions.WalletPersistException;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.repositories.CompanyWalletRepository;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static me.exrates.openapi.models.enums.ActionType.SUBTRACT;

@Service
public class CompanyWalletService {

    private final CompanyWalletRepository companyWalletRepository;

    @Autowired
    public CompanyWalletService(CompanyWalletRepository companyWalletRepository) {
        this.companyWalletRepository = companyWalletRepository;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void deposit(CompanyWallet companyWallet, BigDecimal commissionAmount) {
        companyWallet.setBalance(companyWallet.getBalance());
        companyWallet.setCommissionBalance(companyWallet.getCommissionBalance().add(commissionAmount));
        if (!companyWalletRepository.update(companyWallet)) {
            throw new WalletPersistException("Failed to make deposit to company wallet: " + companyWallet.toString());
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount) {
        BigDecimal newReservedBalance = BigDecimalProcessingUtil.doAction(companyWallet.getCommissionBalance(), amount, SUBTRACT);
        if (newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on company account for operation!" + companyWallet.toString());
        }
        companyWallet.setCommissionBalance(newReservedBalance);
        if (!companyWalletRepository.update(companyWallet)) {
            throw new WalletPersistException("Failed to withdraw from company wallet: " + companyWallet.toString());
        }
    }

    @Transactional(readOnly = true)
    public CompanyWallet findByCurrency(Currency currency) {
        return companyWalletRepository.findByCurrencyId(currency.getId());
    }

    @Transactional
    public boolean subtractCommissionBalanceById(Integer id, BigDecimal amount) {
        return companyWalletRepository.subtarctCommissionBalanceById(id, amount);
    }
}
