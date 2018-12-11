package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

    Merchant findById(int id);

    Merchant findByName(String name);

    List<Merchant> findAll();

    List<Merchant> findAllByCurrency(int currencyId);

    BigDecimal getMinSum(int merchant, int currency);

    Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId);

    List<MerchantCurrency> findAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType);

    List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole, List<String> merchantProcessTypes);

    List<TransferMerchantApiDto> findTransferMerchants();

    List<Integer> findCurrenciesIdsByType(List<String> processTypes);

    List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes);

    void toggleSubtractMerchantCommissionForWithdraw(Integer merchantId, Integer currencyId, boolean subtractMerchantCommissionForWithdraw);

    void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

    void setBlockForAllNonTransfer(OperationType operationType);

    boolean isBlockStateBackupValid(OperationType operationType);

    boolean isBlockStateValid(OperationType operationType);

    void backupBlockState(OperationType operationType);

    void restoreBlockState(OperationType operationType);

    void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

    boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

    List<String> retrieveBtcCoreBasedMerchantNames();

    Optional<CoreWalletDto> retrieveCoreWalletByMerchantName(String merchantName);

    List<CoreWalletDto> retrieveCoreWallets();

    List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime();

    MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

    MerchantCurrencyScaleDto findMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

    boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId);

    void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount);

    List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId);
}