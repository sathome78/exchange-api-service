package me.exrates.openapi.models.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.IntervalType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackDealInterval {

    private Integer intervalValue;
    private IntervalType intervalType;
}
