package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.UserRoleSettings;
import me.exrates.openapi.models.enums.UserRole;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserRoleSettingsRowMapper {

    public static RowMapper<UserRoleSettings> map() {
        return (rs, rowNum) -> UserRoleSettings.builder()
                .userRole(UserRole.convert(rs.getInt("user_role_id")))
                .isOrderAcceptionSameRoleOnly(rs.getBoolean("order_acception_same_role_only"))
                .isBotAcceptionAllowedOnly(rs.getBoolean("bot_acception_allowed"))
                .isManualChangeAllowed(rs.getBoolean("manual_change_allowed"))
                .build();
    }
}
