package me.exrates.openapi.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class TransferResponseDto {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String message;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String balance;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String hash;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String recipient;
}
