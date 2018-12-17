package me.exrates.openapi.model.dto.merchants.neo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeoVout {
    private Integer n;
    private String asset;
    private String value;
    private String address;
}
