package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 19.08.2016.
 */
public class UserAuthenticationDto {

    @NotNull(message = "Email is missing")
    private String email;
    @NotNull(message = "Password is missing")
    private String password;
    @NotNull(message = "Application key is missing")
    private String appKey;

    public UserAuthenticationDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public String toString() {
        return "UserAuthenticationDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", appKey='" + appKey + '\'' +
                '}';
    }
}
