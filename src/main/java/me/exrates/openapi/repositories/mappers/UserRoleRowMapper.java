package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.UserRole;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserRoleRowMapper {

    public static RowMapper<UserRole> map() {
        return (rs, row) -> UserRole.valueOf(rs.getString("role_name"));
    }
}
