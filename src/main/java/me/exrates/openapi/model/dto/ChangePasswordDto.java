package me.exrates.openapi.model.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String password;
    private String confirmPassword;
}
