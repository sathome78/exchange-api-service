package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CurrencyService {

    String getCurrencyName(int currencyId);

    List<Currency> getAllActiveCurrencies();

    List<Currency> getAllCurrencies();

    Currency findByName(String name);

    Currency findById(int id);

    List<Currency> findAllCurrencies();

    void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount, Integer maxDailyRequest);

    void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, Integer maxDailyRequest);

    List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type);

    List<CurrencyPair> getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType type);

    CurrencyPair findCurrencyPairById(int currencyPairId);

    String amountToString(BigDecimal amount, String currency);

    int resolvePrecision(String currency);

    int resolvePrecisionByOperationType(final String currency, OperationType operationType);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds);

    List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForRefill(String userEmail);

    List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForWithdraw(String userEmail);

    List<UserCurrencyOperationPermissionDto> findWithOperationPermissionByUserAndDirection(Integer userId, InvoiceOperationDirection operationDirection);

    Set<String> getCurrencyPermittedNameList(String userEmail);

    List<UserCurrencyOperationPermissionDto> getCurrencyPermittedOperationList(Integer userId);

    Set<String> getCurrencyPermittedNameList(Integer userId);

    List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum);

    Currency getById(int id);

    CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(Integer currencyPairId, OperationType operationType);

    List<CurrencyPairLimitDto> findAllCurrencyLimitsForRoleAndType(String roleName, OrderType orderType);

    void updateCurrencyPairLimit(Integer currencyPairId, OrderType orderType, String roleName, BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount);

    List<CurrencyPairWithLimitsDto> findCurrencyPairsWithLimitsForUser();

    List<Currency> findAllCurrenciesWithHidden();

    BigDecimal computeRandomizedAddition(Integer currencyId, OperationType operationType);

    MerchantCurrencyScaleDto getCurrencyScaleByCurrencyId(Integer currencyId);

    CurrencyPair getCurrencyPairByName(String currencyPair);

    Integer findCurrencyPairIdByName(String pairName);

    List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType);

    List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType ico);

    CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair);

    boolean isIco(Integer currencyId);

    List<CurrencyPairInfoItem> findActiveCurrencyPairs();
}
