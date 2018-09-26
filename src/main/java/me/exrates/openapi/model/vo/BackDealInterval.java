package me.exrates.openapi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.model.enums.IntervalType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackDealInterval {

    private Integer intervalValue;
    private IntervalType intervalType;
}
