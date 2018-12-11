package me.exrates.model.dto.mobileApiDto.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;

import java.util.List;

/**
 * Created by OLEG on 29.06.2017.
 */
@Getter
@Setter
@ToString
public class GeneralInfoDto {
  private List<CurrencyPairWithLimitsDto> currencyPairs;
  private List<MerchantCurrencyApiDto> merchants;
  private CommissionsDto commissions;
  private List<TransferMerchantApiDto> transferMerchants;
  private List<TransferLimitDto> transferLimits;
}
