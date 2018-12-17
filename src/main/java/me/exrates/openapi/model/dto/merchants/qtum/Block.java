package me.exrates.openapi.model.dto.merchants.qtum;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {
    private String hash;
    private Long time;
    private Integer height;
    private List<String> tx;
    private Integer confirmations;
}
