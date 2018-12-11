package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionSourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter
@Setter
public class PendingPaymentFlatForReportDto {
  private int invoiceId;
  private String transactionHash;
  private String address;
//  private PendingPaymentStatusEnum pendingPaymentStatus;
  private LocalDateTime statusUpdateDate;
  private LocalDateTime acceptanceTime;
  private String hash;
  private String merchant;

  private String userNickname;
  private String userEmail;
  private String acceptanceUserEmail;

  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private LocalDateTime datetime;
  private Integer confirmation;
  private Boolean provided;
  private TransactionSourceType sourceType;

  private String currency;
}
