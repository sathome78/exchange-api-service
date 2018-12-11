package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

public interface CommissionService {

  Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole);

  Commission getDefaultCommission(OperationType operationType);

  BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType);

    BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId);

}
