package me.exrates.openapi.repositories;

import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.repositories.mappers.CommissionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CommissionDao {

    private static final String GET_COMMISSION_SQL = "SELECT c.id, c.operation_type, c.date, c.value" +
            " FROM COMMISSION c" +
            " WHERE c.operation_type = :operation_type AND c.user_role = :role_id";

    private static final String GET_DEFAULT_COMMISSION_SQL = "SELECT c.id, c.operation_type, c.date, c.value" +
            " FROM COMMISSION c" +
            " WHERE c.operation_type = :operation_type AND c.user_role = 4";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CommissionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public Commission getCommission(OperationType operationType, UserRole userRole) {
        return jdbcTemplate.queryForObject(
                GET_COMMISSION_SQL,
                Map.of(
                        "operation_type", operationType.getType(),
                        "role_id", userRole.getRole()),
                CommissionRowMapper.map());
    }

    //+
    public Commission getDefaultCommission(OperationType operationType) {
        return jdbcTemplate.queryForObject(
                GET_DEFAULT_COMMISSION_SQL,
                Map.of("operation_type", operationType.getType()),
                CommissionRowMapper.map());
    }
}
