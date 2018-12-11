package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 28.11.2016.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MerchantCurrencyOptionsDto {
    private Integer merchantId;
    private Integer currencyId;
    private String merchantName;
    private String currencyName;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal transferCommission;
    private BigDecimal minFixedCommission;
    private Boolean isRefillBlocked;
    private Boolean isWithdrawBlocked;
    private Boolean isTransferBlocked;
    private Boolean withdrawAutoEnabled;
    private Integer withdrawAutoDelaySeconds;
    private BigDecimal withdrawAutoThresholdAmount;
    private Boolean isMerchantCommissionSubtractedForWithdraw;

}
