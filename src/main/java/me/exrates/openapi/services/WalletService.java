package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.dto.OrderDetailDto;
import me.exrates.openapi.models.dto.WalletsForOrderAcceptionDto;
import me.exrates.openapi.models.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.models.dto.openAPI.WalletBalanceDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.WalletTransferStatus;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.WalletDao;
import me.exrates.openapi.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    private final WalletDao walletDao;
    private final UserService userService;

    @Autowired
    public WalletService(WalletDao walletDao,
                         UserService userService) {
        this.walletDao = walletDao;
        this.userService = userService;
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
    @Transactional
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        return walletDao.getOrderRelatedDataAndBlock(orderId);
    }

    //+
    @Transactional
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        return walletDao.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
    }

    //+
    @Transactional(readOnly = true)
    public List<WalletBalanceDto> getUserBalances() {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        return walletDao.getUserBalances(userEmail);
    }

    //+
    @Transactional
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        return walletDao.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
    }

    //+
    @Transactional(propagation = Propagation.NESTED)
    public int createNewWallet(Wallet wallet) {
        return walletDao.createNewWallet(wallet);
    }

    //+
    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId,
                                                    BigDecimal amount,
                                                    TransactionSourceType sourceType,
                                                    int sourceId,
                                                    String description) {
        return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
    }

    //+
    public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
        return walletDao.walletBalanceChange(walletOperationData);
    }

    //+
    @Transactional(readOnly = true)
    public int getWalletId(int userId, int currencyId) {
        return walletDao.getWalletId(userId, currencyId);
    }
}
