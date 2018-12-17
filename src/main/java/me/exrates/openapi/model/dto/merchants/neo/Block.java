package me.exrates.openapi.model.dto.merchants.neo;


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
    private Integer index;
    private List<NeoTransaction> tx;
    private Integer confirmations;
}
