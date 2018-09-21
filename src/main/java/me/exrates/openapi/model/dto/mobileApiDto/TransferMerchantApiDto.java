package me.exrates.openapi.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@ToString
public class TransferMerchantApiDto {
  private Integer merchantId;
  private String name;
  private Boolean isVoucher;
  private Boolean recipientUserIsNeeded;
  private List<Integer> blockedForCurrencies;
  
  @JsonIgnore
  private String serviceBeanName;
}
