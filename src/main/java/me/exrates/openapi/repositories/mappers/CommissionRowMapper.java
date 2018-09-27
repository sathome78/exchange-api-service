package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.mobileApiDto.dashboard.CommissionDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CommissionRowMapper {

    public static RowMapper<CommissionDto> map() {
        return (rs, row) -> CommissionDto.builder()
                .sellCommission(rs.getBigDecimal("sell_commission"))
                .buyCommission(rs.getBigDecimal("buy_commission"))
                .inputCommission(rs.getBigDecimal("input_commission"))
                .outputCommission(rs.getBigDecimal("output_commission"))
                .transferCommission(rs.getBigDecimal("transfer_commission"))
                .build();
    }
}
