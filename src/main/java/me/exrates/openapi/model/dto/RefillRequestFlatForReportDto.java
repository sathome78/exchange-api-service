package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class RefillRequestFlatForReportDto {
  private int id;
  private String address;
  private String recipientBankName;
  private String userFullName;
  private RefillStatusEnum status;
  private LocalDateTime acceptanceTime;

  private String userNickname;
  private String userEmail;
  private String adminEmail;

  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private LocalDateTime datetime;
  private String merchant;

  private String currency;
  private TransactionSourceType sourceType;
}
