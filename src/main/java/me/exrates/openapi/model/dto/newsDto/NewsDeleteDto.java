package me.exrates.openapi.model.dto.newsDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class NewsDeleteDto {
    private Integer id;
    private String callbackMessage;
}
