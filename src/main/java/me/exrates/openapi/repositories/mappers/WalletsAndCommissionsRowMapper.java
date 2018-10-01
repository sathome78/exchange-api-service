package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.WalletsAndCommissionsDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class WalletsAndCommissionsRowMapper {

    public static RowMapper<WalletsAndCommissionsDto> map() {
        return (rs, rowNum) -> WalletsAndCommissionsDto.builder()
                .userId(rs.getInt("user_id"))
                .spendWalletId(rs.getInt("wallet_id"))
                .spendWalletActiveBalance(rs.getBigDecimal("active_balance"))
                .commissionId(rs.getInt("commission_id"))
                .commissionValue(rs.getBigDecimal("commission_value"))
                .build();
    }
}
