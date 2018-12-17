package me.exrates.openapi.service;

import me.exrates.openapi.model.Commission;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.UserRole;

import java.math.BigDecimal;

public interface CommissionService {

  Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole);

  Commission getDefaultCommission(OperationType operationType);

  BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType);

    BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId);

}
