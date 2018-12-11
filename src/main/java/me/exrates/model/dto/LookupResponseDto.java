package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Maks on 09.10.2017.
 */
@Data
@Builder
public class LookupResponseDto {

    private String country;
    private String operator;
    private boolean isOperable;
}
