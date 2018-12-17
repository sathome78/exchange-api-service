package me.exrates.openapi.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseResult {
    private boolean status;
    private String message;
}
