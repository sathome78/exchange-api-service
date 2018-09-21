package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class TransferRequestFlatDto {
  private int id;
  private BigDecimal amount;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateCreation;
  private TransferStatusEnum status;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime statusModificationDate;
  private Integer merchantId;
  private Integer currencyId;
  private Integer userId;
  private Integer recipientId;
  private BigDecimal commissionAmount;
  private Integer commissionId;
  private String hash;
  private String initiatorEmail;
  private String merchantName;
  private String creatorEmail;
  private String recipientEmail;
  private String currencyName;
  private InvoiceOperationPermission invoiceOperationPermission;
}
