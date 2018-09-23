package me.exrates.openapi.service;

import me.exrates.openapi.dao.CommissionDao;
import me.exrates.openapi.model.Commission;
import me.exrates.openapi.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommissionService {

    @Autowired
    CommissionDao commissionDao;

    public Commission getDefaultCommission(OperationType operationType) {
        return commissionDao.getDefaultCommission(operationType);
    }
}
