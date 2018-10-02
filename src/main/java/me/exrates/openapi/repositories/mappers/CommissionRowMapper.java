package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.enums.OperationType;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CommissionRowMapper {

    public static RowMapper<Commission> map() {
        return (rs, i) -> Commission.builder()
                .id(rs.getInt("id"))
                .dateOfChange(rs.getDate("date"))
                .operationType(OperationType.convert(rs.getInt("operation_type")))
                .value(rs.getBigDecimal("value"))
                .build();
    }
}
