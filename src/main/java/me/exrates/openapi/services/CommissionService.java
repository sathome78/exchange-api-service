package me.exrates.openapi.services;

import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.CommissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommissionService {

    private final CommissionRepository commissionRepository;

    @Autowired
    public CommissionService(CommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

    @Transactional(readOnly = true)
    public Commission getCommission(OperationType operationType, UserRole userRole) {
        return commissionRepository.getCommission(operationType, userRole);
    }

    @Transactional(readOnly = true)
    public Commission getDefaultCommission(OperationType operationType) {
        return commissionRepository.getDefaultCommission(operationType);
    }
}
