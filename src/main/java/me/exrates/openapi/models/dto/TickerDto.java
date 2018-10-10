package me.exrates.openapi.models.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("TickerResponse")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickerDto {

    @ApiModelProperty(value = "ticker items", position = 1, required = true)
    private List<TickerItemDto> tickerItems;
}
