package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by OLEG on 24.03.2017.
 */
@Getter @Setter
@ToString
public class BtcTransactionHistoryDto {
  private String txId;
  private String address;
  private String category;
  private String amount;
  private String blockhash;
  private String fee;
  private Integer confirmations;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime time;
}
