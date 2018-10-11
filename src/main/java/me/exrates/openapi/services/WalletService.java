package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.aspects.Loggable;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.dto.OrderDetailDto;
import me.exrates.openapi.models.dto.WalletBalanceDto;
import me.exrates.openapi.models.dto.WalletsForOrderAcceptionDto;
import me.exrates.openapi.models.dto.WalletsForOrderCancelDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.WalletTransferStatus;
import me.exrates.openapi.models.vo.WalletOperationData;
import me.exrates.openapi.repositories.WalletRepository;
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

    private final WalletRepository walletRepository;
    private final UserService userService;

    @Autowired
    public WalletService(WalletRepository walletRepository,
                         UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    @Loggable(caption = "Get user wallet balances")
    @Transactional(readOnly = true)
    public List<WalletBalanceDto> getUserBalances() {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        return walletRepository.getUserBalances(userEmail);
    }

    @Loggable(caption = "Get user wallets for order by order id and user id and block it")
    @Transactional(readOnly = true)
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        return walletRepository.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
    }

    @Loggable(caption = "Process of creation new wallet")
    @Transactional(propagation = Propagation.NESTED)
    public int createNewWallet(Wallet wallet) {
        return walletRepository.createNewWallet(wallet);
    }

    @Loggable(caption = "Process of wallet inner transferring")
    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId,
                                                    BigDecimal amount,
                                                    TransactionSourceType sourceType,
                                                    int sourceId,
                                                    String description) {
        return walletRepository.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
    }

    @Loggable(caption = "Process of changing wallet balance")
    @Transactional
    public WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData) {
        return walletRepository.walletBalanceChange(walletOperationData);
    }

    @Loggable(caption = "Get wallet id")
    @Transactional(readOnly = true)
    public int getWalletId(int userId, int currencyId) {
        return walletRepository.getWalletId(userId, currencyId);
    }

    @Loggable(caption = "Get order related data and block it")
    @Transactional
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        return walletRepository.getOrderRelatedDataAndBlock(orderId);
    }

    @Loggable(caption = "Check sufficiency of money")
    @Transactional(readOnly = true, propagation = Propagation.NESTED)
    public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
        BigDecimal balance = walletRepository.getWalletABalance(walletId);
        boolean result = balance.compareTo(amountForCheck) >= 0;
        if (!result) {
            log.error(String.format("Not enough wallet money: wallet id %s, actual amount %s but needed %s", walletId,
                    BigDecimalProcessingUtil.formatNonePoint(balance, false),
                    BigDecimalProcessingUtil.formatNonePoint(amountForCheck, false)));
        }
        return result;
    }

    @Loggable(caption = "Get wallet for order by id and operation type and block it")
    @Transactional(readOnly = true)
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        return walletRepository.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
    }
}
