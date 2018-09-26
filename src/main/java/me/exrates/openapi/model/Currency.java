package me.exrates.openapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    private int id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
}
