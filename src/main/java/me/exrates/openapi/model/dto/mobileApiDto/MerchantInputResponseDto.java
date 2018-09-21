package me.exrates.openapi.model.dto.mobileApiDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.MerchantApiResponseType;

/**
 * Created by OLEG on 19.10.2016.
 */
@Getter @Setter
@ToString
public class MerchantInputResponseDto {
    private MerchantApiResponseType type;
    private String walletNumber;
    private Object data;
    private String qr;
    private String additionalTag;
}
