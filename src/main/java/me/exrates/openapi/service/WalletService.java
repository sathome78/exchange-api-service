package me.exrates.openapi.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.WalletDao;
import me.exrates.openapi.model.Wallet;
import me.exrates.openapi.model.dto.OrderDetailDto;
import me.exrates.openapi.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.openapi.model.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.model.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.WalletTransferStatus;
import me.exrates.openapi.model.vo.WalletOperationData;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletDao walletDao;
    @Autowired
    private UserService userService;

    //+
    public int getWalletId(int userId, int currencyId) {
        return walletDao.getWalletId(userId, currencyId);
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletABalance(int walletId) {
        return walletDao.getWalletABalance(walletId);
    }

    //+
    @Transactional(readOnly = true)
    public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
        BigDecimal balance = getWalletABalance(walletId);
        boolean result = balance.compareTo(amountForCheck) >= 0;
        if (!result) {
            log.error(String.format("Not enough wallet money: wallet id %s, actual amount %s but needed %s", walletId,
                    BigDecimalProcessingUtil.formatNonePoint(balance, false),
                    BigDecimalProcessingUtil.formatNonePoint(amountForCheck, false)));
        }
        return result;
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public int createNewWallet(Wallet wallet) {
        return walletDao.createNewWallet(wallet);
    }

    //+
    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
        return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
    }

    //+
    public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
        return walletDao.walletBalanceChange(walletOperationData);
    }

    //+
    @Transactional(readOnly = true)
    public List<WalletBalanceDto> getBalancesForUser() {
        String userEmail = userService.getUserEmailFromSecurityContext();
        return walletDao.getBalancesForUser(userEmail);
    }

    //+
    @Transactional
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        return walletDao.getOrderRelatedDataAndBlock(orderId);
    }

    //+
    @Transactional
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        return walletDao.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
    }

    //+
    @Transactional
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        return walletDao.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
    }
}
