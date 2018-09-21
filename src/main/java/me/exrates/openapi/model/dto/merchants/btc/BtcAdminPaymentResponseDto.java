package me.exrates.openapi.model.dto.merchants.btc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@ToString
public class BtcAdminPaymentResponseDto {
    private String newBalance;
    private List<BtcPaymentResultDetailedDto> results;
}
