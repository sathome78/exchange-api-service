package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.enums.UserRole;
import me.exrates.openapi.models.enums.UserStatus;
import org.springframework.jdbc.core.RowMapper;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserRowMapper {

    public static RowMapper<User> map() {
        return (resultSet, i) -> User.builder()
                .id(resultSet.getInt("id"))
                .nickname(resultSet.getString("nickname"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .regdate(resultSet.getDate("regdate"))
                .phone(resultSet.getString("phone"))
                .status(UserStatus.convert(resultSet.getInt("status")))
                .role(UserRole.valueOf(resultSet.getString("role_name")))
                .finpassword(resultSet.getString("finpassword"))
                .parentEmail(isNull(resultSet.getString("parent_email")) ? null : resultSet.getString("parent_email"))
                .build();
    }
}
