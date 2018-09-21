package me.exrates.openapi.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.MerchantCurrency;

/**
 * Created by OLEG on 27.06.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CryptoAddressDto {
  
  private Integer merchantId;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String mainAddress;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String address;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String additionalFieldName;
  
  public CryptoAddressDto(MerchantCurrency merchantCurrency) {
    merchantId = merchantCurrency.getMerchantId();
    mainAddress = merchantCurrency.getMainAddress();
    address = merchantCurrency.getAddress();
    additionalFieldName = merchantCurrency.getAdditionalFieldName();
  }
}
