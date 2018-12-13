package me.exrates.model.dto;

import lombok.Data;

/**
 * Created by Maks on 02.10.2017.
 */
@Data
public class NotificationResultDto {

    private String messageSource;
    private String[] arguments;


    public NotificationResultDto(String messageSource, String[] arguments) {
        this.messageSource = messageSource;
        this.arguments = arguments;
    }
}
