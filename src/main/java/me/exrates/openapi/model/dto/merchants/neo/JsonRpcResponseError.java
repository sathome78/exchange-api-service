package me.exrates.openapi.model.dto.merchants.neo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonRpcResponseError {
    private Integer code;
    private String message;
}
