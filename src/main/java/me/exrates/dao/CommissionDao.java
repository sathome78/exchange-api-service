package me.exrates.dao;

import me.exrates.model.Commission;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface CommissionDao {

	Commission getCommission(OperationType operationType, UserRole userRole);

    Commission getDefaultCommission(OperationType operationType);

	BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType);

  BigDecimal getCommissionMerchant(Integer merchantId, Integer currencyId, OperationType operationType);

  List<Commission> getEditableCommissions();

    List<CommissionShortEditDto> getEditableCommissionsByRoles(List<Integer> roleIds, Locale locale);

    void updateMerchantCurrencyCommission(EditMerchantCommissionDto editMerchantCommissionDto);

    void updateCommission(Integer id, BigDecimal value);

    void updateCommission(OperationType operationType, List<Integer> roleIds, BigDecimal value);

    BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId);

  Commission getCommissionById(Integer commissionId);
}

