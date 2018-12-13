package me.exrates.model.dto.mobileApiDto;

import java.time.LocalDateTime;

/**
 * Created by OLEG on 09.09.2016.
 */
public class TemporaryPasswordDto {
    private Long id;
    private Integer userId;
    private String password;
    private LocalDateTime dateCreation;
    private Integer temporalTokenId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Integer getTemporalTokenId() {
        return temporalTokenId;
    }

    public void setTemporalTokenId(Integer temporalTokenId) {
        this.temporalTokenId = temporalTokenId;
    }

    @Override
    public String toString() {
        return "TemporaryPasswordDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", password='" + password + '\'' +
                ", dateCreation=" + dateCreation +
                ", temporalTokenId=" + temporalTokenId +
                '}';
    }
}
