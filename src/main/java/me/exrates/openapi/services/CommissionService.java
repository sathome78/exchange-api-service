package me.exrates.openapi.services;

import me.exrates.openapi.repositories.CommissionDao;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
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
