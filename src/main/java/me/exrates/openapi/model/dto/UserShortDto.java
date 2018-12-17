package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.enums.UserStatus;

@Getter
@Setter
@ToString
public class UserShortDto {
    private Integer id;
    private String email;
    private String password;
    private UserStatus status;
}
