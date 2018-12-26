package me.exrates.openapi.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CallbackURL {
    private String callbackURL;
    private Integer pairId;

    public CallbackURL(){

    }
}
