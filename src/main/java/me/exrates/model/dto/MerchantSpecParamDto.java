package me.exrates.model.dto;

import lombok.Data;

/**
 * Created by maks on 09.06.2017.
 */
@Data
public class MerchantSpecParamDto {

    private int id;
    private int merchantId;
    private String paramName;
    private String paramValue;
}
