package me.exrates.openapi.dao;

import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.CurrencyLimit;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.dto.CurrencyPairLimitDto;
import me.exrates.openapi.model.dto.MerchantCurrencyScaleDto;
import me.exrates.openapi.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.openapi.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.openapi.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.openapi.model.enums.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CurrencyDao {

    List<Currency> getAllActiveCurrencies();

    String getCurrencyName(int currencyId);

    Currency findByName(String name);

    Currency findById(int id);

    List<Currency> findAllCurrencies();

    List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, Integer maxDailyRequest);

    void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, Integer maxDailyRequest);

    List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type);

    CurrencyPair findCurrencyPairById(int currencyPairId);

    List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection);

    List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId);

    List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum);

    CurrencyPair findCurrencyPairByOrderId(int orderId);

    CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId);

    List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId);

    void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId,
                              BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount);

    List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId);

    List<Currency> findAllCurrenciesWithHidden();

    MerchantCurrencyScaleDto findCurrencyScaleByCurrencyId(Integer currencyId);

    CurrencyPair findCurrencyPairByName(String currencyPair);

    List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType);

    List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType);

    CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair);

    boolean isCurrencyIco(Integer currencyId);

    List<CurrencyPairInfoItem> findActiveCurrencyPairs();

    Optional<Integer> findOpenCurrencyPairIdByName(String pairName);
}