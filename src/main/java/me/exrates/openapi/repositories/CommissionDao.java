package me.exrates.openapi.repositories;

import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class CommissionDao {

    private static final RowMapper<Commission> commissionRowMapper = (resultSet, i) -> {
        Commission commission = new Commission();
        commission.setDateOfChange(resultSet.getDate("date"));
        commission.setId(resultSet.getInt("id"));
        commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
        commission.setValue(resultSet.getBigDecimal("value"));
        return commission;
    };

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    //+
    public Commission getCommission(OperationType operationType, UserRole userRole) {
        final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.value " +
                "FROM COMMISSION " +
                "WHERE operation_type = :operation_type AND user_role = :role_id";
        final HashMap<String, Integer> params = new HashMap<>();
        params.put("operation_type", operationType.type);
        params.put("role_id", userRole.getRole());
        return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
    }

    //+
    public Commission getDefaultCommission(OperationType operationType) {
        final String sql = "SELECT id, operation_type, date, value " +
                "FROM COMMISSION " +
                "WHERE operation_type = :operation_type AND user_role = 4;";
        final HashMap<String, Integer> params = new HashMap<>();
        params.put("operation_type", operationType.type);
        return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
    }
}
