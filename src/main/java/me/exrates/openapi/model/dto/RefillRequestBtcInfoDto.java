package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 25.05.2017.
 */
@Getter
@Setter
@ToString
public class RefillRequestBtcInfoDto {
  private Integer id;
  private String address;
  private String txId;
  private BigDecimal amount;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateCreation;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateModification;
  private String userEmail;
  private String status;
  
}
