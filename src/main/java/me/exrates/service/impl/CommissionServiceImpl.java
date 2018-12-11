package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CommissionServiceImpl implements CommissionService {
  @Autowired
  CommissionDao commissionDao;

  @Autowired
  UserService userService;

  @Autowired
  UserRoleService userRoleService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Override
  public Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
    return commissionDao.getCommission(operationType, userRole);
  }

  @Override
  public Commission getDefaultCommission(OperationType operationType) {
    return commissionDao.getDefaultCommission(operationType);
  }

  @Override
  @Transactional
  public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
    if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT)) {
      throw new IllegalArgumentException("Invalid operation type");
    }
    return commissionDao.getCommissionMerchant(merchant, currency, operationType);
  }

    @Override
  public BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId) {
    return commissionDao.getMinFixedCommission(currencyId, merchantId);
  }



}
