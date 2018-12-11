package me.exrates.model.dto;

import me.exrates.model.enums.UserIpState;

import java.time.LocalDateTime;

/**
 * Created by Valk on 03.05.2016.
 */
public class UserIpDto {
    private int userId;
    private UserIpState userIpState;
    private LocalDateTime registrationDate;
    private LocalDateTime confirmDate;
    private LocalDateTime LastRegistrationDate;

    /*constructors*/
    private UserIpDto(){};

    public UserIpDto(int userId) {
        this.userId = userId;
        this.userIpState = UserIpState.NEW;
    }

    /*getters setters*/

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserIpState getUserIpState() {
        return userIpState;
    }

    public void setUserIpState(UserIpState userIpState) {
        this.userIpState = userIpState;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(LocalDateTime confirmDate) {
        this.confirmDate = confirmDate;
    }

    public LocalDateTime getLastRegistrationDate() {
        return LastRegistrationDate;
    }

    public void setLastRegistrationDate(LocalDateTime lastRegistrationDate) {
        LastRegistrationDate = lastRegistrationDate;
    }
}
