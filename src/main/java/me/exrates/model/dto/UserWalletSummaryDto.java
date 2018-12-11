package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 04.05.2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserWalletSummaryDto {
    private Integer userRoleId;
    private String currencyName;
    private int walletsAmount;
    private BigDecimal balance;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private BigDecimal balancePerWallet;
    private BigDecimal activeBalancePerWallet;
    private BigDecimal reservedBalancePerWallet;
    private BigDecimal merchantAmountInput;
    private BigDecimal merchantAmountOutput;

    public UserWalletSummaryDto(UserWalletSummaryDto userWalletSummaryDto) {
        this.userRoleId = userWalletSummaryDto.userRoleId;
        this.currencyName = userWalletSummaryDto.currencyName;
        this.walletsAmount = userWalletSummaryDto.walletsAmount;
        this.balance = userWalletSummaryDto.balance;
        this.activeBalance = userWalletSummaryDto.activeBalance;
        this.reservedBalance = userWalletSummaryDto.reservedBalance;
        this.balancePerWallet = userWalletSummaryDto.balancePerWallet;
        this.activeBalancePerWallet = userWalletSummaryDto.activeBalancePerWallet;
        this.reservedBalancePerWallet = userWalletSummaryDto.reservedBalancePerWallet;
        this.merchantAmountInput = userWalletSummaryDto.merchantAmountInput;
        this.merchantAmountOutput = userWalletSummaryDto.merchantAmountOutput;
    }

    public void calculate(){
        balance = BigDecimalProcessing.doAction(activeBalance, reservedBalance, ActionType.ADD);
        activeBalancePerWallet = BigDecimalProcessing.doAction(activeBalance, BigDecimal.valueOf(walletsAmount), ActionType.DEVIDE);
        reservedBalancePerWallet = BigDecimalProcessing.doAction(reservedBalance, BigDecimal.valueOf(walletsAmount), ActionType.DEVIDE);
        balancePerWallet = BigDecimalProcessing.doAction(balance, BigDecimal.valueOf(walletsAmount), ActionType.DEVIDE);
    }

    public void increment(UserWalletSummaryDto item){
        walletsAmount = walletsAmount + item.getWalletsAmount();
        activeBalance = activeBalance.add(item.getActiveBalance());
        reservedBalance = reservedBalance.add(item.getReservedBalance());
        merchantAmountInput = merchantAmountInput.add(item.getMerchantAmountInput());
        merchantAmountOutput = merchantAmountOutput.add(item.getMerchantAmountOutput());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWalletSummaryDto that = (UserWalletSummaryDto) o;

        return currencyName != null ? currencyName.equals(that.currencyName) : that.currencyName == null;

    }

    @Override
    public int hashCode() {
        return currencyName != null ? currencyName.hashCode() : 0;
    }
}
