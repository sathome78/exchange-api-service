package me.exrates.openapi.services;

import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.CommissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommissionService {

    private final CommissionDao commissionDao;

    @Autowired
    public CommissionService(CommissionDao commissionDao) {
        this.commissionDao = commissionDao;
    }

    @Transactional(readOnly = true)
    public Commission getCommission(OperationType operationType, UserRole userRole) {
        return commissionDao.getCommission(operationType, userRole);
    }

    @Transactional(readOnly = true)
    public Commission getDefaultCommission(OperationType operationType) {
        return commissionDao.getDefaultCommission(operationType);
    }
}
