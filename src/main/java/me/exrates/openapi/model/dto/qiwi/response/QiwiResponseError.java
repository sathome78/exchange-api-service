package me.exrates.openapi.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseError {
    private String code;
    private String message;
    private String field;
}
